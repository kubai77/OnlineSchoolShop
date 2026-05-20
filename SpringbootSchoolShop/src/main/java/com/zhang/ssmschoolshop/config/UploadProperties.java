package com.zhang.ssmschoolshop.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "upload")
public class UploadProperties {

    private String path = "/usr/upload";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}