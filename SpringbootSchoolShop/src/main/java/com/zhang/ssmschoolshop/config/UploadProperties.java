package com.zhang.ssmschoolshop.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件上传配置属性
 */
@Component
@ConfigurationProperties(prefix = "upload")
public class UploadProperties {

    /**
     * 上传文件保存的本地路径
     */
    private String path = "/usr/upload";

    /**
     * 静态资源访问路径模式
     */
    private String pathPattern = "/pictures/**";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPathPattern() {
        return pathPattern;
    }

    public void setPathPattern(String pathPattern) {
        this.pathPattern = pathPattern;
    }
}
