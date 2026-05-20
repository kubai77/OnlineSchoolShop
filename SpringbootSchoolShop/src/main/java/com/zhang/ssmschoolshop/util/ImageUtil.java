package com.zhang.ssmschoolshop.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author created by Zhangdazhuang
 * @version v.0.1
 * @Description 文件保存工具类
 * @date 2019/4/30
 * @备注
 **/
@Component
public class ImageUtil {

    private static String uploadPath;

    @Value("${file.upload-path}")
    public void setUploadPath(String uploadPath) {
        ImageUtil.uploadPath = uploadPath;
    }

    public static String imagePath(MultipartFile file, String shopName) {
        if (file.isEmpty()) {
            return "false";
        }
        
        String path = uploadPath;
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        String fileName=UUID.randomUUID().toString().substring(0,4)+shopName;
        File dest = new File(path + "/" +fileName);
        System.out.println("保存的绝对路径为:"+dest);
        if (!dest.getParentFile().exists()) { //判断文件父目录是否存在
            dest.getParentFile().mkdir();
        }
        try {
            //根据系统的不同，保存到不同的路径
            file.transferTo(dest);
            return fileName;
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "false";
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "false";
        }


    }
}
