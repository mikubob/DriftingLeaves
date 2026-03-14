package com.xuan.service;

import cn.hutool.db.PageResult;
import com.xuan.dto.RssSubscriptionDTO;
import com.xuan.dto.RssSubscriptionPageQueryDTO;
import com.xuan.entity.RssSubscriptions;
import com.xuan.vo.RssSubscriptionStatusVO;

import java.util.List;

public interface RssSubscriptionService {
    /**
     * 添加RSS订阅
     * @param rssSubscriptionDTO
     */
    void addSubscription(RssSubscriptionDTO rssSubscriptionDTO);

    /**
     * 分页查询RSS订阅列表
     * @param rssSubscriptionPageQueryDTO
     * @return
     */
    PageResult pageQuery(RssSubscriptionPageQueryDTO rssSubscriptionPageQueryDTO);

    /**
     * 更新RSS订阅
     * @param rssSubscriptions
     */
    void updateSubscription(RssSubscriptions rssSubscriptions);

    /**
     * 批量删除RSS订阅
     * @param ids
     */
    void batchDelete(List<Long> ids);

    /**
     * 根据ID查询RSS订阅
     * @param id
     * @return
     */
    RssSubscriptions getById(Long id);

    /**
     * 获取所有激活的订阅
     * @return
     */
    List<RssSubscriptions> getAllActiveSubscriptions();

    /**
     * 根据邮箱取消订阅
     * @param email
     */
    void unsubscribeByEmail(String email);

    /**
     * 检查访客是否已订阅
     * @param visitorId
     * @return
     */
    boolean hasSubscribed(Long visitorId);

    /**
     * 获取访客订阅详情
     * @param visitorId
     * @return
     */
    RssSubscriptionStatusVO getSubscriptionStatus(Long visitorId);
}
