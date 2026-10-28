package com.xuan.service;

/**
 * 邮件服务，发送验证码、回复提醒、邮箱确认和含退订链接的文章通知。
 */
public interface EmailService {
    /**
     * 发送邮箱所有者确认邮件，成功确认前不激活订阅。
     * @param email 收件邮箱
     * @param nickname 订阅者昵称
     * @param url 绑定当前访客及待确认邮箱的确认链接
     */
    void sendSubscriptionConfirmation(String email, String nickname, String url);
    
    /**
     * 发送验证码邮件
     * @param toEmail
     * @param code
     */
    void sendVerifyCode(String toEmail, String code);

    /**
     * 发送评论/留言回复通知邮件
     * @param toEmail 收件人邮箱
     * @param parentNickname 被回复人昵称
     * @param parentContent 被回复的内容
     * @param replyNickname 回复人昵称
     * @param replyContent 回复内容
     * @param type 类型：message-留言 / comment-文章评论
     */
    void sendReplyNotification(String toEmail, String parentNickname, String parentContent,
                               String replyNickname, String replyContent, String type);

    /**
     * 发送新文章通知邮件
     * @param toEmail 收件人邮箱
     * @param nickname 订阅者昵称
     * @param articleTitle 文章标题
     * @param articleSummary 文章摘要
     * @param articleUrl 文章链接
     * @param unsubscribeUrl 绑定订阅记录及确认版本的退订链接
     */
    void sendNewArticleNotification(String toEmail, String nickname, String articleTitle,
                                    String articleSummary, String articleUrl, String unsubscribeUrl);
}
