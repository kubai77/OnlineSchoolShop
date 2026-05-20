package com.zhang.ssmschoolshop.util;

import com.zhang.ssmschoolshop.config.ImageStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class ImageUtil {

    private static ImageStorageProperties imageStorageProperties;

    @Autowired
    public ImageUtil(ImageStorageProperties imageStorageProperties) {
        ImageUtil.imageStorageProperties = imageStorageProperties;
    }

    public static String imagePath(MultipartFile file, String shopName) {
        if (file.isEmpty()) {
            return "false";
        }
        String uploadDir = imageStorageProperties == null ? "" : imageStorageProperties.getUploadDir();
        if (uploadDir.isEmpty()) {
            return "false";
        }
        String fileName = UUID.randomUUID().toString().substring(0, 4) + shopName;
        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }
        File dest = new File(uploadDirectory, fileName);
        System.out.println("保存的绝对路径为:" + dest);
        try {
            file.transferTo(dest);
            return fileName;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            return "false";
        } catch (IOException e) {
            e.printStackTrace();
            return "false";
        }
    }
}
