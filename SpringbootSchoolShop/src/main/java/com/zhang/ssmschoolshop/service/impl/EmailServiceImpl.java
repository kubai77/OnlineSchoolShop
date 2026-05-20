package com.zhang.ssmschoolshop.service.impl;


import com.zhang.ssmschoolshop.entity.EmailSend;
import com.zhang.ssmschoolshop.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * @author codingzx
 * @description 发送邮件的服务
 * @link sendEmailToAdmin  用户下单后 发送邮件给管理员
 * @link sendEmailToUser   管理员发货后通知用户
 * @date 2021/7/24 12:59
 */
@Service("emailService")
@Component
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Value("${mail.receive}")
    private String adminEmail;

    @Value("${mail.enabled:false}")
    private boolean mailEnabled;

    @Autowired
    MailSender mailSender;

    public EmailServiceImpl() {
    }

    public EmailServiceImpl(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmailToAdmin(String orderDetails) {
        if (!mailEnabled) {
            log.info("邮件发送未开启，跳过通知管理员");
            return;
        }
        EmailSend emailSend = new EmailSend();
        emailSend.setSubject("用户购买信息");
        emailSend.setContent("今天是 " + LocalDate.now() + ",有新用户购买。详情: " + orderDetails);
        log.info("开始发送邮件给管理员: {}", adminEmail);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(adminEmail);
        message.setSubject(emailSend.getSubject());
        message.setText(emailSend.getContent());
        try {
            mailSender.send(message);
            log.info("邮件发送成功给管理员");
        } catch (MailException e) {
            log.error("给管理员发送邮件失败", e);
        }

    }

    @Override
    public void sendEmailToUser(String userEmail) {
        if (!mailEnabled) {
            log.info("邮件发送未开启，跳过通知用户");
            return;
        }
        if (userEmail == null || userEmail.isEmpty()) {
            log.warn("用户邮箱为空，跳过发送发货通知");
            return;
        }

        EmailSend emailSend = new EmailSend();
        emailSend.setSubject("订单发货通知");
        emailSend.setContent("今天是 " + LocalDate.now() + ",您在商城的订单已经发货，请注意查收。");
        log.info("开始发送邮件给用户: {}", userEmail);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(userEmail);
        message.setSubject(emailSend.getSubject());
        message.setText(emailSend.getContent());
        try {
            mailSender.send(message);
            log.info("邮件发送成功给用户: {}", userEmail);
        } catch (MailException e) {
            log.error("给用户 {} 发送邮件失败", userEmail, e);
        }
    }
}
