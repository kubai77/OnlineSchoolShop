package com.zhang.ssmschoolshop.interceptor;

import com.zhang.ssmschoolshop.entity.Admin;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 后台管理员拦截器：校验 session 中是否存在 admin 属性
 */
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            // AJAX 请求返回 JSON，普通请求重定向到登录页
            String requestedWith = request.getHeader("X-Requested-With");
            if ("XMLHttpRequest".equals(requestedWith)) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":1,\"msg\":\"请先登录\"}");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/login");
            }
            return false;
        }
        return true;
    }
}
