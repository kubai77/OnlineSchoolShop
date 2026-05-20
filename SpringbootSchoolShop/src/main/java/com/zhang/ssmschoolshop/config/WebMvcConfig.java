package com.zhang.ssmschoolshop.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private static final List<String> ADMIN_EXCLUDE_PATHS = Arrays.asList(
            "/admin/login",
            "/admin/confirmLogin",
            "/admin/logout"
    );

    private static final List<String> USER_PROTECTED_PATHS = Arrays.asList(
            "/information",
            "/saveInfo",
            "/info/**",
            "/saveAddr",
            "/deleteAddr",
            "/insertAddr",
            "/deleteList",
            "/savePsw",
            "/finishList",
            "/logout",
            "/order",
            "/orderFinish",
            "/addCart",
            "/showcart",
            "/cartjson",
            "/deleteCart/**",
            "/update",
            "/collect",
            "/deleteCollect",
            "/comment"
    );

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionAuthInterceptor("admin", "/admin/login", "请先登录管理员账号"))
                .addPathPatterns("/admin/**")
                .excludePathPatterns(ADMIN_EXCLUDE_PATHS);

        registry.addInterceptor(new SessionAuthInterceptor("user", "/login", "请先登录"))
                .addPathPatterns(USER_PROTECTED_PATHS);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String os = System.getProperty("os.name");
        String pathPatterns = "/pictures/**";
        String pathAbsolute = "file:D:/upload/";
        if (!os.toLowerCase().startsWith("windows")) {
            pathAbsolute = "file:/usr/upload/";
        }
        registry.addResourceHandler(pathPatterns).addResourceLocations(pathAbsolute);
    }
}
