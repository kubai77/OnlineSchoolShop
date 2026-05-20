package com.zhang.ssmschoolshop.util;

import com.zhang.ssmschoolshop.config.UploadProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class ImageUtil {

    private final String uploadPath;

    public ImageUtil(UploadProperties uploadProperties) {
        this.uploadPath = uploadProperties.getPath();
    }

    public String imagePath(MultipartFile file, String shopName) {
        if (file.isEmpty()) {
            return "false";
        }
        String fileName = UUID.randomUUID().toString().substring(0, 4) + shopName;
        File dest = new File(uploadPath + "/" + fileName);
        System.out.println("保存的绝对路径为:" + dest);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdir();
        }
        try {
            file.transferTo(dest);
            return fileName;
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
            return "false";
        }
    }
}