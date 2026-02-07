package com.hnau.platform.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传工具类
 */
@Component
public class FileUploadUtil {

    // 基础上传目录
    private static final String BASE_UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "upload";

    // 允许的文件后缀名列表
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", // 图片文件
            ".pdf", ".doc", ".docx", ".txt", // 文档文件
            ".zip", ".rar", ".7z" // 压缩文件
    );

    /**
     * 上传文件
     */
    public static String uploadFile(MultipartFile file, String uploadDir) throws IOException {
        // 验证文件
        validateFile(file);

        // 确保上传目录存在
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;

        // 保存文件
        File destFile = new File(uploadDir + File.separator + filename);
        file.transferTo(destFile);

        // 返回文件路径
        return filename;
    }

    /**
     * 验证文件
     */
    private static void validateFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("文件不能为空");
        }

        // 检查文件大小（最大100MB）
        if (file.getSize() > 100 * 1024 * 1024) {
            throw new IOException("文件大小不能超过100MB");
        }

        // 检查文件后缀名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new IOException("文件格式不支持");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        // 打印文件后缀名，用于调试
        System.out.println("文件后缀名: " + extension);
        System.out.println("允许的文件后缀名: " + ALLOWED_EXTENSIONS);

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IOException("文件格式不支持，仅支持：" + String.join(", ", ALLOWED_EXTENSIONS));
        }
    }

    /**
     * 上传头像
     */
    public String uploadAvatar(MultipartFile file, String grade, String major) throws IOException {
        // 验证文件
        validateFile(file);

        // 处理grade和major的空值情况
        if (grade == null || grade.isEmpty()) {
            grade = "default";
        }
        if (major == null || major.isEmpty()) {
            major = "default";
        }

        // 构建头像上传目录：/upload/avatar/年级/专业/
        String avatarDir = BASE_UPLOAD_DIR + File.separator + "avatar" + File.separator + grade + File.separator
                + major;
        // 确保目录存在
        File directory = new File(avatarDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;

        // 保存文件
        File destFile = new File(avatarDir + File.separator + filename);
        file.transferTo(destFile);

        // 返回完整的头像路径
        return "avatar/" + grade + "/" + major + "/" + filename;
    }

    /**
     * 删除文件
     */
    public boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }
}