package com.zhang.ssmschoolshop.service;

/**
 * @author codingzx
 * @description
 * @date 2021/7/24 12:58
 */
public interface EmailService {
    /**
     * 用户购买成功后发送邮件通知管理员
     * @param userEmail 下单用户的邮箱，用于记录来源（可选）
     */
    void sendEmailToAdmin(String userEmail);

    /**
     * 管理员发货后发送邮件通知用户
     * @param userEmail 要通知的用户的邮箱
     */
    void sendEmailToUser(String userEmail);
}
