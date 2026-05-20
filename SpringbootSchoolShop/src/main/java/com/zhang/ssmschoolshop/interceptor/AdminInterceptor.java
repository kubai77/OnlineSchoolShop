package com.zhang.ssmschoolshop.interceptor;

import com.zhang.ssmschoolshop.entity.Admin;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;

public class AdminInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin != null) {
            return true;
        }

        // 判断是否为 ajax 请求
        String requestType = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equals(requestType)) {
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.print("{\"code\": 200, \"msg\": \"请先登录\"}");
            out.flush();
            out.close();
        } else {
            response.sendRedirect(request.getContextPath() + "/admin/login");
        }
        return false;
    }
}
