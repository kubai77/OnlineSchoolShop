package com.zhang.ssmschoolshop.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhang.ssmschoolshop.util.Msg;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SessionAuthInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String sessionAttribute;
    private final String loginPath;
    private final String failMessage;

    public SessionAuthInterceptor(String sessionAttribute, String loginPath, String failMessage) {
        this.sessionAttribute = sessionAttribute;
        this.loginPath = loginPath;
        this.failMessage = failMessage;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HttpSession session = request.getSession(false);
        Object currentUser = session == null ? null : session.getAttribute(sessionAttribute);
        if (currentUser != null) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        if (requiresBodyResponse(handlerMethod)) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(Msg.fail(failMessage)));
            return false;
        }

        response.sendRedirect(request.getContextPath() + loginPath);
        return false;
    }

    private boolean requiresBodyResponse(HandlerMethod handlerMethod) {
        return handlerMethod.hasMethodAnnotation(ResponseBody.class)
                || AnnotatedElementUtils.hasAnnotation(handlerMethod.getBeanType(), ResponseBody.class);
    }
}
