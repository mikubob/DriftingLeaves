package com.xuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xuan.dto.RssSubscriptionDTO;
import com.xuan.dto.RssSubscriptionPageQueryDTO;
import com.xuan.entity.RssSubscriptions;
import com.xuan.result.PageResult;
import com.xuan.vo.RssSubscriptionStatusVO;

import java.util.List;

/**
 * RSS 订阅服务，负责邮箱确认、访客绑定、管理维护及凭据退订。
 */
public interface IRssSubscriptionService extends IService<RssSubscriptions> {
    /**
     * 为当前访客新增或更新订阅；首次或换邮箱发送确认邮件，同邮箱已确认订阅仅更新昵称。
     * @param rssSubscriptionDTO 昵称及目标邮箱，visitorId 不参与授权
     */
    void addSubscription(RssSubscriptionDTO rssSubscriptionDTO);

    /**
     * 管理端分页查询订阅列表。
     * @param rssSubscriptionPageQueryDTO 邮箱及状态筛选条件
     * @return 订阅分页
     */
    PageResult pageQuery(RssSubscriptionPageQueryDTO rssSubscriptionPageQueryDTO);

    /**
     * 管理端修改允许维护的订阅字段；换邮箱撤销确认，未确认邮箱不能直接启用。
     * @param rssSubscriptions ID、昵称、邮箱及启用状态
     * @throws com.xuan.exception.RssSubscriptionException 目标不存在或状态不合法
     */
    void updateSubscription(RssSubscriptions rssSubscriptions);

    /**
     * 管理端批量删除订阅。
     * @param ids 订阅 ID 列表
     */
    void batchDelete(List<Long> ids);

    /**
     * 管理端按 ID 获取订阅资料。
     * @param id 订阅 ID
     * @return 订阅记录，不存在时为 null
     */
    RssSubscriptions getRssWithId(Long id);

    /**
     * 获取已启用且邮箱已确认的文章通知收件人。
     * @return 按订阅时间倒序的有效订阅
     */
    List<RssSubscriptions> getAllActiveSubscriptions();

    /**
     * 凭签名令牌退订，邮箱或确认版本变更后旧凭据失效，重复退订幂等。
     * @param token 签名退订凭据
     * @throws com.xuan.exception.RssSubscriptionException 凭据无效或绑定已变更
     */
    void unsubscribe(String token);

    /**
     * 确认当前访客发起的邮箱票据，提交成功后才消费票据。
     * @param token 二十四小时有效的确认票据
     * @throws com.xuan.exception.RssSubscriptionException 票据无效或不属于当前访客
     */
    void confirm(String token);

    /**
     * 签发绑定订阅记录、邮箱及确认版本的退订凭据。
     * @param subscription 订阅记录
     * @return 邮件链接使用的签名凭据
     */
    String unsubscribeToken(RssSubscriptions subscription);

    /**
     * 检查当前请求访客是否拥有已启用且已确认的订阅。
     * @param visitorId 仅兼容旧参数，实际身份取自 VisitorContext
     * @return 是否已订阅
     * @throws com.xuan.exception.TokenException 缺少访客身份
     */
    boolean hasSubscribed(Long visitorId);

    /**
     * 读取当前访客的有效订阅详情及绑定版本的退订凭据。
     * @param visitorId 仅兼容旧参数，实际身份取自 VisitorContext
     * @return 订阅状态，未订阅时不携带邮箱及凭据
     * @throws com.xuan.exception.TokenException 缺少访客身份
     */
    RssSubscriptionStatusVO getSubscriptionStatus(Long visitorId);
}
