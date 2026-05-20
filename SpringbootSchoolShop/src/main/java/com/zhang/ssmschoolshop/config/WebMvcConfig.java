package com.zhang.ssmschoolshop.config;


import com.zhang.ssmschoolshop.interceptor.AdminInterceptor;
import com.zhang.ssmschoolshop.interceptor.UserInterceptor;
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
        // 后台管理员拦截器
        registry.addInterceptor(new AdminInterceptor())
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login", "/admin/confirmLogin", "/admin/logout", "/admin/css/**", "/admin/js/**", "/admin/img/**", "/admin/fonts/**");

        // 前台用户中心拦截器
        registry.addInterceptor(new UserInterceptor())
                .addPathPatterns(
                        "/information", "/saveInfo", "/info/address", "/saveAddr", "/deleteAddr", "/insertAddr",
                        "/info/list", "/deleteList", "/info/favorite", "/savePsw", "/finishList", "/collect", "/deleteCollect",
                        "/addCart", "/showcart", "/cartjson", "/update", "/deleteCart/**", "/order", "/orderFinish"
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