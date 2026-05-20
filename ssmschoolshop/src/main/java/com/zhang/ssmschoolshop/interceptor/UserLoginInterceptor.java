package com.zhang.ssmschoolshop.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 前台用户登录拦截器：用户中心相关接口要求用户已登录。
 * 纳入拦截的路径模式：
 *   /info/**          — 用户中心（个人信息、地址管理、订单列表、收藏夹）
 *   /saveInfo         — 修改个人信息
 *   /saveAddr         — 修改收货地址
 *   /deleteAddr        — 删除收货地址
 *   /insertAddr        — 新增收货地址
 *   /savePsw          — 修改密码
 *   /deleteList        — 删除订单
 *   /finishList        — 确认收货
 *   /addCart          — 加入购物车
 *   /showcart         — 查看购物车页面
 *   /cartjson         — 获取购物车JSON
 *   /update           — 更新购物车数量 (CartController)
 *   /deleteCart/**     — 删除购物车商品
 *   /order            — 订单确认页
 *   /orderFinish      — 提交订单
 *   /collect          — 收藏商品
 *   /deleteCollect     — 取消收藏
 *   /comment          — 发表评论
 */
public class UserLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Object user = session.getAttribute("user");

        if (user == null) {
            String requestedWith = request.getHeader("X-Requested-With");
            if ("XMLHttpRequest".equals(requestedWith)) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"msg\":\"请先登录\"}");
            } else {
                response.sendRedirect(request.getContextPath() + "/login");
            }
            return false;
        }
        return true;
    }
}