package com.hnau.platform.controller;

import com.hnau.platform.common.Result;
import com.hnau.platform.utils.FileUploadUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * 文件控制器
 */
@RestController
@RequestMapping("/file")
public class FileController {
    
    @Value("${platform.file.upload-path}")
    private String uploadDir;
    
    /**
     * 上传文件
     */
    @PostMapping("/upload")
    public Result uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 上传文件
            String filename = FileUploadUtil.uploadFile(file, uploadDir);
            // 返回文件路径
            return Result.success("/uploads/" + filename);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败");
        }
    }
}