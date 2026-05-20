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
 * @link sendEmailToUser   管理员发送后通知用户
 * @date 2021/7/24 12:59
 */
@Service("emailService")
@Component
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Value("${mail.from-address}")
    private String fromAddress;

    @Value("${mail.admin-address}")
    private String adminAddress;

    @Value("${mail.user-address}")
    private String userAddress;

    @Autowired
    MailSender mailSender;

    public EmailServiceImpl() {
    }

    public EmailServiceImpl(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmailToAdmin() {
        EmailSend emailSend = new EmailSend();
        emailSend.setSubject("用户购买信息");
        emailSend.setContent("today is " + LocalDate.now() + ",有新用户购买");
        log.info("开始发送邮件给管理员");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(adminAddress);
        message.setSubject(emailSend.getSubject());
        message.setText(emailSend.getContent());
        try {
            mailSender.send(message);
            log.info("发送邮件给管理员成功");
        } catch (MailException e) {
            log.error("发送邮件给管理员失败", e);
        }

    }

    @Override
    public void sendEmailToUser() {
        EmailSend emailSend = new EmailSend();
        emailSend.setSubject("管理员已经发货");
        emailSend.setContent("today is " + LocalDate.now() + ",商城已经发货");
        log.info("开始发送邮件给用户");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(userAddress);
        message.setSubject(emailSend.getSubject());
        message.setText(emailSend.getContent());
        try {
            mailSender.send(message);
            log.info("发送邮件给用户成功");
        } catch (MailException e) {
            log.error("发送邮件给用户失败", e);
        }
    }
}
