package com.zhang.ssmschoolshop.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * @author created by CodingZhangxin
 * @version v.0.1
 * @Description TODO
 * @date 2019/5/10
 * @备注  springboot内置tomcat配置虚拟路径
 *      上传路径由 application.yml 中的 upload.path 配置驱动
 **/

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private UploadProperties uploadProperties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String pathPatterns = uploadProperties.getPathPattern();
        String pathAbsolute = "file:" + uploadProperties.getPath() + "/";
        registry.addResourceHandler(pathPatterns).addResourceLocations(pathAbsolute);

    }

}