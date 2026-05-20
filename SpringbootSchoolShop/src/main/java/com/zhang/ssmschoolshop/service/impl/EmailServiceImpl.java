package com.zhang.ssmschoolshop.service.impl;


import com.zhang.ssmschoolshop.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author codingzx
 * @description 发送邮件的服务
 * @link sendEmailToAdmin  用户下单后发送邮件通知管理员
 * @link sendEmailToUser   管理员发货后发送邮件通知用户
 * @date 2021/7/24 12:59
 */
@Service("emailService")
@Component
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Value("${mail.username}")
    private String senderEmail;

    @Value("${mail.receive:}")
    private String adminReceiverEmail;

    @Value("${mail.enabled:true}")
    private boolean mailEnabled;

    @Autowired
    private MailSender mailSender;

    public EmailServiceImpl() {
    }

    public EmailServiceImpl(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmailToAdmin(String userEmail) {
        if (!mailEnabled) {
            log.info("[Email] 邮件发送已禁用，跳过向管理员发送邮件");
            return;
        }
        if (adminReceiverEmail == null || adminReceiverEmail.isEmpty()) {
            log.warn("[Email] 管理员收件邮箱未配置，邮件发送取消");
            return;
        }

        log.info("[Email] 准备发送邮件至管理员: {}", adminReceiverEmail);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(adminReceiverEmail);
        message.setSubject("用户购买信息");
        message.setText("有新用户下单，购买日期: " + java.time.LocalDate.now());
        try {
            mailSender.send(message);
            log.info("[Email] 管理员邮件发送成功");
        } catch (MailAuthenticationException e) {
            log.error("[Email] 邮件发送失败，认证异常: {}", e.getMessage());
        } catch (MailSendException e) {
            log.error("[Email] 邮件发送失败，发送异常: {}", e.getMessage());
        } catch (MailException e) {
            log.error("[Email] 邮件发送失败: {}", e.getMessage());
        }
    }

    @Override
    public void sendEmailToUser(String userEmail) {
        if (!mailEnabled) {
            log.info("[Email] 邮件发送已禁用，跳过向用户发送邮件");
            return;
        }
        if (userEmail == null || userEmail.isEmpty()) {
            log.warn("[Email] 用户邮箱为空，邮件发送取消");
            return;
        }

        log.info("[Email] 准备发送邮件至用户: {}", userEmail);
        SimpleMailMessage message = new SimpleMailMessage();
        // 管理员视角发送，From 为系统发件人（管理员邮箱），To 为用户邮箱
        message.setFrom(adminReceiverEmail != null ? adminReceiverEmail : senderEmail);
        message.setTo(userEmail);
        message.setSubject("管理员已发货");
        message.setText("商城已于 " + java.time.LocalDate.now() + " 发货，请注意查收");
        try {
            mailSender.send(message);
            log.info("[Email] 用户邮件发送成功");
        } catch (MailAuthenticationException e) {
            log.error("[Email] 邮件发送失败，认证异常: {}", e.getMessage());
        } catch (MailSendException e) {
            log.error("[Email] 邮件发送失败，发送异常: {}", e.getMessage());
        } catch (MailException e) {
            log.error("[Email] 邮件发送失败: {}", e.getMessage());
        }
    }
}
