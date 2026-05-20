package com.zhang.ssmschoolshop.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "shop.image")
public class ImageStorageProperties {

    private String uploadDir;

    public String getUploadDir() {
        return uploadDir == null ? "" : uploadDir.trim();
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String getResourceLocation() {
        String normalizedPath = getUploadDir().replace("\\", "/");
        if (!normalizedPath.endsWith("/")) {
            normalizedPath = normalizedPath + "/";
        }
        return "file:" + normalizedPath;
    }
}
