package com.zhang.ssmschoolshop.interceptor;

import com.zhang.ssmschoolshop.entity.User;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 前台用户拦截器：校验 session 中是否存在 user 属性
 */
public class UserInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            String requestedWith = request.getHeader("X-Requested-With");
            if ("XMLHttpRequest".equals(requestedWith)) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":1,\"msg\":\"请先登录\"}");
            } else {
                response.sendRedirect(request.getContextPath() + "/login");
            }
            return false;
        }
        return true;
    }
}
