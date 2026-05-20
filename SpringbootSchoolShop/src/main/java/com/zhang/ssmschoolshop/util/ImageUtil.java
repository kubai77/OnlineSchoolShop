package com.zhang.ssmschoolshop.util;

import com.zhang.ssmschoolshop.config.UploadProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class ImageUtil {

    @Autowired
    private UploadProperties uploadProperties;

    public String imagePath(MultipartFile file, String shopName) {
        if (file.isEmpty()) {
            return "false";
        }
        int size = (int) file.getSize();
        String path = uploadProperties.getPath();
        String fileName = UUID.randomUUID().toString().substring(0, 4) + shopName;
        File dest = new File(path + "/" + fileName);
        System.out.println("保存的绝对路径为:" + dest);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
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
