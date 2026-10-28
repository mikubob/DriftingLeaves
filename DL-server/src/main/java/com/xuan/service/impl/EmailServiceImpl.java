package com.xuan.service.impl;

import com.xuan.constant.MessageConstant;
import com.xuan.exception.EmailSendErrorException;
import com.xuan.properties.EmailProperties;
import com.xuan.properties.WebsiteProperties;
import com.xuan.service.EmailService;
import com.xuan.utils.EmailTemplateUtil;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * 邮件服务实现，使用安全模板发送验证码、回复、订阅确认及文章通知。
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailProperties emailProperties;
    private final WebsiteProperties websiteProperties;

    @Override
    public void sendSubscriptionConfirmation(String email, String nickname, String url) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(emailProperties.getFrom(), emailProperties.getPersonal());
            helper.setTo(email);
            helper.setSubject(websiteProperties.getTitle() + " - 确认RSS订阅");
            helper.setText("<p>" + EmailTemplateUtil.escape(nickname) + "，请在24小时内确认订阅：</p><p><a href='"
                    + EmailTemplateUtil.escape(url) + "'>确认订阅</a></p><p>若非本人操作，请忽略此邮件。</p>", true);
            mailSender.send(message);
        } catch (Exception ex) {
            log.error("发送订阅确认邮件失败");
            throw new EmailSendErrorException(MessageConstant.EMAIL_SEND_ERROR);
        }
    }

    /**
     * 发送验证码
     * @param toEmail 接收方邮箱
     * @param code 验证码
     */
    @Override
    public void sendVerifyCode(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(emailProperties.getFrom(), emailProperties.getPersonal());
            helper.setTo(toEmail);
            helper.setSubject("【" + websiteProperties.getTitle() + "】管理端-验证码");
            helper.setText(EmailTemplateUtil.buildSendVerifyCodeEmailContent(code, websiteProperties), true);
            mailSender.send(message);
        } catch (Exception e){
            log.error("发送验证码邮件失败 to={}, ex={}", toEmail, e.getMessage());
            throw new EmailSendErrorException(MessageConstant.EMAIL_SEND_ERROR);
        }
    }

    /**
     * 发送回复通知
     * @param toEmail 收件人邮箱
     * @param parentNickname 被回复人昵称
     * @param parentContent 被回复的内容
     * @param replyNickname 回复人昵称
     * @param replyContent 回复内容
     * @param type 类型：message-留言 / comment-文章评论
     */
    @Override
    public void sendReplyNotification(String toEmail, String parentNickname, String parentContent, String replyNickname, String replyContent, String type) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(emailProperties.getFrom(), emailProperties.getPersonal());
            helper.setTo(toEmail);
            String typeText = "comment".equals(type) ? "文章评论" : "留言";
            helper.setSubject(websiteProperties.getTitle() + " - 您的" + typeText + "收到了新回复");
            helper.setText(EmailTemplateUtil.buildReplyNotificationEmailContent(parentNickname, parentContent,
                    replyNickname, replyContent, typeText, websiteProperties), true);
            mailSender.send(message);
            log.info("发送回复通知邮件成功: to={}, type={}", toEmail, type);
        } catch (Exception e) {
            log.error("发送回复通知邮件失败 to={}, type={}, ex={}", toEmail, type, e.getMessage());
        }
    }

    /**
     * 发送新文章通知
     * @param toEmail 收件人邮箱
     * @param nickname 订阅者昵称
     * @param articleTitle 文章标题
     * @param articleSummary 文章摘要
     * @param articleUrl 文章链接
     * @param unsubscribeUrl 绑定订阅记录及确认版本的退订链接
     */
    @Override
    public void sendNewArticleNotification(String toEmail, String nickname, String articleTitle, String articleSummary, String articleUrl, String unsubscribeUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(emailProperties.getFrom(), emailProperties.getPersonal());
            helper.setTo(toEmail);
            helper.setSubject(websiteProperties.getTitle() + " - 新文章发布：" + articleTitle);
            helper.setText(EmailTemplateUtil.buildNewArticleNotificationEmailContent(nickname, articleTitle, articleSummary, articleUrl, unsubscribeUrl, websiteProperties), true);
            mailSender.send(message);
            log.info("发送新文章通知邮件成功: to={}, title={}", toEmail, articleTitle);
        } catch (Exception e) {
            log.error("发送新文章通知邮件失败 to={}, title={}, ex={}", toEmail, articleTitle, e.getMessage());
        }
    }
}
