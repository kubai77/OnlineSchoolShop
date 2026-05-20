package com.zhang.ssmschoolshop.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "upload")
public class UploadProperties {
    private String path = "D:/upload";
    private String accessPathPattern = "/pictures/**";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAccessPathPattern() {
        return accessPathPattern;
    }

    public void setAccessPathPattern(String accessPathPattern) {
        this.accessPathPattern = accessPathPattern;
    }
}
