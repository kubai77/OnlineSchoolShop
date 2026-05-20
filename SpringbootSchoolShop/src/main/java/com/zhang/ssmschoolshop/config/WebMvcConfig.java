package com.zhang.ssmschoolshop.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * @author created by CodingZhangxin
 * @version v.0.1
 * @Description TODO
 * @date 2019/5/10
 * @备注  springboot内置tomcat配置虚拟路径
 **/

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String pathPatterns="/pictures/**";
        String pathAbsolute="file:" + uploadPath;
        if (!pathAbsolute.endsWith("/")) {
            pathAbsolute += "/";
        }
        registry.addResourceHandler(pathPatterns).addResourceLocations(pathAbsolute);
    }

}