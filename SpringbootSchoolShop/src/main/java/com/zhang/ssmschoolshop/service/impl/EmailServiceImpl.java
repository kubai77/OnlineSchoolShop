package com.zhang.ssmschoolshop.service.impl;

import com.zhang.ssmschoolshop.entity.EmailSend;
import com.zhang.ssmschoolshop.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Service("emailService")
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Value("${mail.from:${mail.username}}")
    private String senderAddress;

    @Value("${mail.receive:}")
    private String adminRecipientAddress;

    @Value("${mail.receive2:}")
    private String userRecipientAddress;

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmailToAdmin() {
        sendEmail("用户购买信息", "today is " + LocalDate.now() + ",有新用户购买", adminRecipientAddress, "order-created");
    }

    @Override
    public void sendEmailToUser() {
        sendEmail("管理员已经发货", "today is " + LocalDate.now() + ",商城已经发货", userRecipientAddress, "order-shipped");
    }

    private void sendEmail(String subject, String content, String recipientAddress, String scene) {
        if (!StringUtils.hasText(senderAddress) || !StringUtils.hasText(recipientAddress)) {
            log.error("邮件发送配置缺失，scene={}, from={}, to={}", scene, senderAddress, recipientAddress);
            return;
        }

        EmailSend emailSend = new EmailSend();
        emailSend.setSubject(subject);
        emailSend.setContent(content);
        emailSend.setReceivers(new String[]{recipientAddress});

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderAddress);
        message.setTo(emailSend.getReceivers());
        message.setSubject(emailSend.getSubject());
        message.setText(emailSend.getContent());

        try {
            mailSender.send(message);
            log.info("邮件发送成功，scene={}, from={}, to={}, subject={}", scene, senderAddress, recipientAddress, subject);
        } catch (MailException e) {
            log.error("邮件发送失败，scene={}, from={}, to={}, subject={}", scene, senderAddress, recipientAddress, subject, e);
        }
    }
}
