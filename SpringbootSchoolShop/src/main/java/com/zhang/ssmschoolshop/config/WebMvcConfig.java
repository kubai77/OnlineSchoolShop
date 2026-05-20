package com.zhang.ssmschoolshop.config;


import com.zhang.ssmschoolshop.interceptor.AdminLoginInterceptor;
import com.zhang.ssmschoolshop.interceptor.UserLoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * @author created by CodingZhangxin
 * @version v.0.1
 * @Description TODO
 * @date 2019/5/10
 * @备注  springboot内置tomcat配置虚拟路径
 *      linux： /usr/upload  /pictures
 *      window:  d:/upload  /pictures
 **/

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 后台管理拦截器：/admin/** 默认要求管理员登录，放行登录相关接口
        registry.addInterceptor(new AdminLoginInterceptor())
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login", "/admin/confirmLogin");

        // 前台用户中心拦截器：用户相关操作要求用户登录
        registry.addInterceptor(new UserLoginInterceptor())
                .addPathPatterns(
                        "/info/**",
                        "/saveInfo",
                        "/saveAddr",
                        "/deleteAddr",
                        "/insertAddr",
                        "/savePsw",
                        "/deleteList",
                        "/finishList",
                        "/addCart",
                        "/showcart",
                        "/cartjson",
                        "/update",
                        "/deleteCart/**",
                        "/order",
                        "/orderFinish",
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
            // todo mac需要修改地址
            pathAbsolute="file:/usr/upload/";
        }
        registry.addResourceHandler(pathPatterns).addResourceLocations(pathAbsolute);

    }

}