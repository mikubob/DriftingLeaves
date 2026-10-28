package com.xuan.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xuan.context.VisitorContext;
import com.xuan.dto.RssSubscriptionDTO;
import com.xuan.dto.RssSubscriptionPageQueryDTO;
import com.xuan.entity.RssSubscriptions;
import com.xuan.exception.RssSubscriptionException;
import com.xuan.mapper.RssSubscriptionMapper;
import com.xuan.mapper.VisitorMapper;
import com.xuan.properties.JwtProperties;
import com.xuan.properties.WebsiteProperties;
import com.xuan.result.PageResult;
import com.xuan.service.EmailService;
import com.xuan.service.IRssSubscriptionService;
import com.xuan.utils.CommitActions;
import com.xuan.utils.DiscussionUtil;
import com.xuan.utils.JwtUtil;
import com.xuan.vo.RssSubscriptionQueryVO;
import com.xuan.vo.RssSubscriptionStatusVO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * RSS 订阅服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RssSubscriptionServiceImpl extends ServiceImpl<RssSubscriptionMapper, RssSubscriptions> implements IRssSubscriptionService {
    private final StringRedisTemplate redis;
    private final EmailService emailService;
    private final WebsiteProperties website;
    private final JwtProperties jwt;
    private final VisitorMapper visitors;

    private static final DefaultRedisScript<Long> DELETE_PENDING =
            new DefaultRedisScript<>(
                    "if redis.call('GET',KEYS[1]) == ARGV[1] then return redis.call('DEL',KEYS[1]) end return 0", Long.class);


    /**
     * 添加 RSS 订阅
     * @param rssSubscriptionDTO 订阅数据
     */
    @Override
    public void addSubscription(RssSubscriptionDTO rssSubscriptionDTO) {
        Long visitorId = VisitorContext.requireId();
        String email = rssSubscriptionDTO.getEmail().trim().toLowerCase(Locale.ROOT);
        RssSubscriptions current = lambdaQuery().eq(RssSubscriptions::getVisitorId, visitorId)
                .orderByDesc(RssSubscriptions::getId).last("LIMIT 1").one();
        if (current != null && email.equalsIgnoreCase(current.getEmail()) && Integer.valueOf(1).equals(current.getConfirmed())
                && Integer.valueOf(1).equals(current.getIsActive())) {
            lambdaUpdate().eq(RssSubscriptions::getId, current.getId()).set(RssSubscriptions::getNickname, rssSubscriptionDTO.getNickname()).update();
            return;
        }
        String ticket = UUID.randomUUID().toString();
        String pending = JSON.toJSONString(Map.of("visitorId", visitorId,
                "email", email, "nickname", rssSubscriptionDTO.getNickname() == null ? "" : rssSubscriptionDTO.getNickname()));
        String key = "rss:confirm:" + ticket;
        redis.opsForValue().set(key, pending, Duration.ofHours(24));
        try {
            emailService.sendSubscriptionConfirmation(email, rssSubscriptionDTO.getNickname(), blogUrl() + "/?rssConfirm=" + ticket);
        } catch (RuntimeException ex) {
            redis.execute(DELETE_PENDING, List.of(key), pending);
            throw ex;
        }
    }

    @Override
    @Transactional
    public void confirm(String token) {
        Long visitorId = VisitorContext.requireId();
        if (token == null || !token.matches("[a-fA-F0-9-]{36}")) throw new RssSubscriptionException("确认链接无效");
        // Serialize changes for one visitor; unique database constraints also protect email ownership races.
        visitors.lockById(visitorId);
        String key = "rss:confirm:" + token;
        String raw = redis.opsForValue().get(key);
        if (raw == null) throw new RssSubscriptionException("确认链接已使用或过期");
        var pending = JSON.parseObject(raw);
        if (!visitorId.equals(pending.getLong("visitorId"))) throw new RssSubscriptionException("请在发起订阅的浏览器确认");
        String email = pending.getString("email");
        RssSubscriptions target = lambdaQuery().eq(RssSubscriptions::getEmail, email).last("LIMIT 1 FOR UPDATE").one();
        RssSubscriptions current = lambdaQuery().eq(RssSubscriptions::getVisitorId, visitorId).last("LIMIT 1 FOR UPDATE").one();
        if (target == null) target = current;
        if (target != null && token.equals(target.getConfirmationId())) {
            CommitActions.afterCommit(() -> redis.execute(DELETE_PENDING, List.of(key), raw));
            return;
        }
        if (current != null && target != null && !current.getId().equals(target.getId())) {
            lambdaUpdate().eq(RssSubscriptions::getId, current.getId()).set(RssSubscriptions::getVisitorId, null)
                    .set(RssSubscriptions::getIsActive, 0).set(RssSubscriptions::getUnSubscribeTime, LocalDateTime.now()).update();
        }
        if (target == null) {
            target = RssSubscriptions.builder().email(email).visitorId(visitorId).nickname(pending.getString("nickname"))
                    .isActive(1).confirmed(1).confirmationId(token).subscribeTime(LocalDateTime.now()).build();
            save(target);
        } else {
            lambdaUpdate().eq(RssSubscriptions::getId, target.getId()).set(RssSubscriptions::getEmail, email)
                    .set(RssSubscriptions::getVisitorId, visitorId).set(RssSubscriptions::getNickname, pending.getString("nickname"))
                    .set(RssSubscriptions::getIsActive, 1).set(RssSubscriptions::getConfirmed, 1)
                    .set(RssSubscriptions::getConfirmationId, token).set(RssSubscriptions::getSubscribeTime, LocalDateTime.now())
                    .set(RssSubscriptions::getUnSubscribeTime, null).update();
        }
        CommitActions.afterCommit(() -> redis.execute(DELETE_PENDING, List.of(key), raw));
    }

    /**
     * 取得确认邮件使用的博客地址，补全协议并去除末尾斜杠。
     * @return 博客根地址
     * @throws RssSubscriptionException 未配置博客地址
     */
    private String blogUrl() {
        String url = website.getBlog();
        if (url == null || url.isBlank()) throw new RssSubscriptionException("博客地址未配置");
        if (!url.startsWith("http://") && !url.startsWith("https://")) url = "https://" + url;
        return url.replaceAll("/+$", "");
    }

    @Override
    public String unsubscribeToken(RssSubscriptions sub) {
        return JwtUtil.createJWT(jwt.getSecretKey(), Duration.ofDays(3650).toMillis(),
                Map.of("purpose", "rss-unsubscribe", "subscriptionId", sub.getId(), "email", sub.getEmail(),
                        "confirmationId", sub.getConfirmationId() == null ? "legacy" : sub.getConfirmationId()));
    }

    /**
     * 分页查询 RSS 订阅
     * @param rssSubscriptionPageQueryDTO 查询参数
     * @return 分页结果
     */
    @Override
    public PageResult pageQuery(RssSubscriptionPageQueryDTO rssSubscriptionPageQueryDTO) {
        // 构建分页条件
        Page<RssSubscriptions> page = new Page<>(rssSubscriptionPageQueryDTO.getPage(), rssSubscriptionPageQueryDTO.getPageSize());

        // 构建查询条件
        LambdaQueryWrapper<RssSubscriptions> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(rssSubscriptionPageQueryDTO.getEmail() != null && !rssSubscriptionPageQueryDTO.getEmail().isEmpty(),
                RssSubscriptions::getEmail, rssSubscriptionPageQueryDTO.getEmail())
                .eq(rssSubscriptionPageQueryDTO.getIsActive() != null,
                        RssSubscriptions::getIsActive, rssSubscriptionPageQueryDTO.getIsActive())
                .orderByDesc(RssSubscriptions::getSubscribeTime);

        // 执行分页查询
        Page<RssSubscriptions> result = page(page, wrapper);

        // 转换为 QueryVO 并返回
        Page<RssSubscriptionQueryVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream()
                .map(sub -> cn.hutool.core.bean.BeanUtil.copyProperties(sub, RssSubscriptionQueryVO.class))
                .toList());
        return PageResult.fromIPage(voPage);
    }

    /**
     * 更新 RSS 订阅
     * @param rssSubscriptions 更新的订阅数据
     */
    @Override
    @Transactional
    public void updateSubscription(RssSubscriptions rssSubscriptions) {
        if (rssSubscriptions.getId() == null || rssSubscriptions.getId() <= 0) throw new RssSubscriptionException("更新ID无效");
        RssSubscriptions current = lambdaQuery().eq(RssSubscriptions::getId, rssSubscriptions.getId()).last("FOR UPDATE").one();
        if (current == null) throw new RssSubscriptionException("订阅不存在");
        boolean emailChanged = rssSubscriptions.getEmail() != null && !rssSubscriptions.getEmail().equalsIgnoreCase(current.getEmail());
        var update = lambdaUpdate().eq(RssSubscriptions::getId, current.getId());
        if (rssSubscriptions.getNickname() != null) update.set(RssSubscriptions::getNickname, rssSubscriptions.getNickname());
        if (emailChanged) {
            String email = rssSubscriptions.getEmail().trim().toLowerCase(Locale.ROOT);
            if (!email.equals(DiscussionUtil.email(email)) || !email.contains("@")) throw new RssSubscriptionException("邮箱格式错误");
            update.set(RssSubscriptions::getEmail, email).set(RssSubscriptions::getConfirmed, 0)
                    .set(RssSubscriptions::getConfirmationId, null).set(RssSubscriptions::getIsActive, 0);
        } else if (rssSubscriptions.getIsActive() != null) {
            int active = rssSubscriptions.getIsActive();
            if (active != 0 && active != 1 || active == 1 && !Integer.valueOf(1).equals(current.getConfirmed())) {
                throw new RssSubscriptionException("订阅状态无效或邮箱尚未确认");
            }
            update.set(RssSubscriptions::getIsActive, active);
        }
        if (!update.update()) throw new RssSubscriptionException("更新目标不存在");
    }

    /**
     * 批量删除 RSS 订阅
     * @param ids 订阅 ID 列表
     */
    @Override
    @Transactional
    public void batchDelete(List<Long> ids) {
        removeBatchByIds(ids);
    }

    /**
     * 根据 ID 查询 RSS 订阅
     * @param id 订阅 ID
     * @return 订阅数据
     */
    @Override
    public RssSubscriptions getRssWithId(Long id) {
        return super.getById(id);
    }

    /**
     * 获取所有激活的订阅
     * @return 激活的订阅列表
     */
    @Override
    public List<RssSubscriptions> getAllActiveSubscriptions() {
        LambdaQueryWrapper<RssSubscriptions> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RssSubscriptions::getIsActive, 1).eq(RssSubscriptions::getConfirmed, 1)
                .orderByDesc(RssSubscriptions::getSubscribeTime);
        return list(wrapper);
    }

    /**
     * 使用绑定订阅记录及邮箱确认版本的签名凭据退订，重复调用保持幂等。
     * @param token 签名退订凭据
     * @throws RssSubscriptionException 凭据无效、过期或绑定已变更
     */
    @Override
    public void unsubscribe(String token) {
        Claims claims;
        Long id;
        try {
            claims = JwtUtil.parseJWT(jwt.getSecretKey(), token);
            if (!"rss-unsubscribe".equals(claims.get("purpose"))) throw new IllegalArgumentException();
            if (claims.get("subscriptionId") == null) throw new IllegalArgumentException();
            id = Long.valueOf(claims.get("subscriptionId").toString());
        } catch (JwtException | IllegalArgumentException ex) {
            throw new RssSubscriptionException("退订链接无效或已过期");
        }
        RssSubscriptions sub = getById(id);
        if (sub == null || !sub.getEmail().equals(claims.get("email"))
                || !Objects.equals(sub.getConfirmationId() == null ? "legacy" : sub.getConfirmationId(), claims.get("confirmationId"))) {
            throw new RssSubscriptionException("退订链接无效或已过期");
        }
        lambdaUpdate().eq(RssSubscriptions::getId, id).eq(RssSubscriptions::getEmail, sub.getEmail())
                .eq(sub.getConfirmationId() != null, RssSubscriptions::getConfirmationId, sub.getConfirmationId())
                .isNull(sub.getConfirmationId() == null, RssSubscriptions::getConfirmationId)
                .set(RssSubscriptions::getIsActive, 0).set(RssSubscriptions::getUnSubscribeTime, LocalDateTime.now()).update();
    }

    /**
     * 检查访客是否已订阅
     * @param visitorId 兼容旧参数，实际身份取自 VisitorContext
     * @return 是否已订阅
     */
    @Override
    public boolean hasSubscribed(Long visitorId) {
        visitorId = VisitorContext.requireId();
        return lambdaQuery()
                .eq(RssSubscriptions::getVisitorId, visitorId)
                .eq(RssSubscriptions::getIsActive, 1).eq(RssSubscriptions::getConfirmed, 1)
                .exists();
    }

    /**
     * 获取访客订阅详情
     * @param visitorId 兼容旧参数，实际身份取自 VisitorContext
     * @return 订阅详情
     */
    @Override
    public RssSubscriptionStatusVO getSubscriptionStatus(Long visitorId) {
        visitorId = VisitorContext.requireId();
        // 查询访客的激活订阅
        RssSubscriptions sub = lambdaQuery()
                .eq(RssSubscriptions::getVisitorId, visitorId)
                .eq(RssSubscriptions::getIsActive, 1).eq(RssSubscriptions::getConfirmed, 1)
                .orderByDesc(RssSubscriptions::getId).last("LIMIT 1").one();

        // 未找到订阅记录，返回未订阅状态
        if (sub == null) {
            return RssSubscriptionStatusVO.builder().subscribed(false).build();
        }

        // 返回订阅详情
        return RssSubscriptionStatusVO.builder()
                .subscribed(true)
                .nickname(sub.getNickname())
                .unsubscribeToken(unsubscribeToken(sub))
                .email(sub.getEmail())
                .build();
    }
}
