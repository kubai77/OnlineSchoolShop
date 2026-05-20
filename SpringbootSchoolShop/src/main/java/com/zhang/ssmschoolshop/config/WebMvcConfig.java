package com.zhang.ssmschoolshop.config;


import com.zhang.ssmschoolshop.interceptor.AdminInterceptor;
import com.zhang.ssmschoolshop.interceptor.UserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 后台管理员拦截：/admin/** 下除登录/登出外全部需要 admin session
        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns("/admin/**")
                .excludePathPatterns(
                        "/admin/login",
                        "/admin/confirmLogin",
                        "/admin/logout"
                );

        // 前台用户拦截：用户中心、购物车、订单、收藏、评论等需要登录的接口
        registry.addInterceptor(new UserInterceptor())
                .addPathPatterns(
                        "/information",
                        "/saveInfo",
                        "/savePsw",
                        "/info/**",
                        "/saveAddr",
                        "/deleteAddr",
                        "/insertAddr",
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
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String os = System.getProperty("os.name");
        String pathPatterns="/pictures/**";
        String pathAbsolute="file:D:/upload/";
        if (!os.toLowerCase().startsWith("windows")){
            pathAbsolute="file:/usr/upload/";
        }
        registry.addResourceHandler(pathPatterns).addResourceLocations(pathAbsolute);

    }

}