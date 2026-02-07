package com.hnau.platform.controller;

import com.hnau.platform.common.Result;
import com.hnau.platform.entity.Material;
import com.hnau.platform.entity.MaterialTag;
import com.hnau.platform.entity.Tag;
import com.hnau.platform.entity.User;
import com.hnau.platform.mapper.MaterialTagMapper;
import com.hnau.platform.mapper.TagMapper;
import com.hnau.platform.mapper.UserMapper;
import com.hnau.platform.service.MaterialService;
import com.hnau.platform.service.IntegralService;
import com.hnau.platform.vo.MaterialDetailVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.ArrayList;
import java.util.List;
import com.hnau.platform.entity.DownloadRecord;
import com.hnau.platform.entity.UploadRecord;
import com.hnau.platform.service.DownloadRecordService;
import com.hnau.platform.service.UploadRecordService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;

/**
 * 资料控制器
 */
@RestController
@RequestMapping("/api/material")
public class MaterialController {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MaterialTagMapper materialTagMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private DownloadRecordService downloadRecordService;

    @Autowired
    private UploadRecordService uploadRecordService;

    @Autowired
    private IntegralService integralService;

    /**
     * 获取资料列表
     */
    @GetMapping("/list")
    public Result getMaterialList(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String tag,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String order,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        List<Material> materials = materialService.getMaterialList(keyword, categoryId, tag, sortBy, order, page,
                pageSize);
        // 构建与前端期望格式匹配的响应
        java.util.Map<String, Object> resultMap = new java.util.HashMap<>();
        resultMap.put("list", materials);
        resultMap.put("total", materialService.getMaterialCount(keyword, categoryId, tag));
        return Result.success(resultMap);
    }

    /**
     * 根据分类获取资料
     */
    @GetMapping("/byCategory")
    public Result getMaterialByCategory(@RequestParam Long categoryId) {
        List<Material> materials = materialService.getByCategoryId(categoryId);
        return Result.success(materials);
    }

    /**
     * 根据用户获取资料
     */
    @GetMapping("/byUser")
    public Result getMaterialByUser(@RequestParam Long userId) {
        List<Material> materials = materialService.getByUserId(userId);
        return Result.success(materials);
    }

    /**
     * 搜索资料
     */
    @GetMapping("/search")
    public Result searchMaterial(@RequestParam String keyword) {
        List<Material> materials = materialService.search(keyword);
        return Result.success(materials);
    }

    /**
     * 获取资料详情
     */
    @GetMapping("/info/{id}")
    public Result getMaterialInfo(@PathVariable Long id) {
        Material material = materialService.getById(id);
        if (material != null) {
            // 增加浏览次数
            materialService.incrementViewCount(id);

            // 查询资料关联的标签
            QueryWrapper<MaterialTag> materialTagWrapper = new QueryWrapper<>();
            materialTagWrapper.eq("material_id", id);
            List<MaterialTag> materialTags = materialTagMapper.selectList(materialTagWrapper);

            // 构建标签列表
            List<String> tags = new ArrayList<>();
            for (MaterialTag materialTag : materialTags) {
                Tag tag = tagMapper.selectById(materialTag.getTagId());
                if (tag != null) {
                    tags.add(tag.getName());
                }
            }

            // 构建MaterialDetailVO
            MaterialDetailVO vo = MaterialDetailVO.fromMaterial(material);
            vo.setTags(tags);

            // 获取上传者用户名
            User user = userMapper.selectById(material.getUserId());
            if (user != null) {
                vo.setUsername(user.getName());
            }

            return Result.success(vo);
        }
        return Result.error("资料不存在");
    }

    /**
     * 上传资料
     */
    @PostMapping("/upload")
    public Result uploadMaterial(@RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("tags") String tags,
            @RequestParam("userId") Long userId) {
        System.out.println("=== 上传资料开始 ===");
        System.out.println("用户ID: " + userId);
        System.out.println("资料名称: " + name);
        Material material = materialService.uploadMaterial(file, name, description, categoryId, tags, userId);
        if (material != null) {
            System.out.println("上传资料成功，资料ID: " + material.getId());
            // 创建上传记录
            UploadRecord uploadRecord = new UploadRecord();
            uploadRecord.setUserId(userId);
            uploadRecord.setMaterialId(material.getId());
            uploadRecord.setPoints(20); // 上传资料获得20积分
            uploadRecord.setUploadTime(java.time.LocalDateTime.now());
            System.out.println("准备保存上传记录: " + uploadRecord);
            boolean saveResult = uploadRecordService.save(uploadRecord);
            System.out.println("保存上传记录结果: " + saveResult);

            return Result.success(material, "上传成功，获得20积分奖励");
        }
        System.out.println("上传资料失败");
        return Result.error("上传失败");
    }

    /**
     * 更新资料
     */
    @PutMapping("/update")
    public Result updateMaterial(@RequestBody Material material) {
        boolean success = materialService.updateById(material);
        if (success) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }

    /**
     * 删除资料
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteMaterial(@PathVariable Long id) {
        boolean success = materialService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 下载资料
     */
    @GetMapping("/download/{id}")
    public void downloadMaterial(@PathVariable Long id, @RequestParam Long userId, HttpServletResponse response) {
        Material material = materialService.getById(id);
        if (material == null) {
            try {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.setContentType("application/json; charset=utf-8");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"code\":404,\"message\":\"资料不存在\",\"data\":null}");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // 检查用户积分
        User user = userMapper.selectById(userId);
        if (user == null) {
            try {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json; charset=utf-8");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"code\":400,\"message\":\"用户不存在\",\"data\":null}");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        // 检查是否是下载自己上传的文件
        boolean isOwnMaterial = userId.equals(material.getUserId());
        int pointsDeducted = 0;

        // 下载其他用户上传的文件时扣减5积分
        if (!isOwnMaterial) {
            pointsDeducted = 5;
            if (user.getPoints() < pointsDeducted) {
                try {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.setContentType("application/json; charset=utf-8");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"code\":400,\"message\":\"积分不足，无法下载\",\"data\":null}");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }

            // 扣减积分
            user.setPoints(user.getPoints() - pointsDeducted);

            // 同时更新Integral表中的积分
            boolean updateSuccess = integralService.updateIntegral(userId, -pointsDeducted, "下载其他用户资料", id, 1);
            System.out.println("更新积分结果: " + updateSuccess);
            
            // 给上传者增加积分（每次被下载获得2积分）
            Long uploaderId = material.getUserId();
            boolean uploaderUpdateSuccess = integralService.updateIntegral(uploaderId, 2, "资料被下载获得积分", id, 1);
            System.out.println("给上传者增加积分结果: " + uploaderUpdateSuccess);
            System.out.println("上传者ID: " + uploaderId + "，获得2积分");
        }

        // 增加下载次数
        user.setDownloadCount(user.getDownloadCount() + 1);

        // 合并更新，避免积分更新被覆盖
        int updateResult = userMapper.updateById(user);
        System.out.println("更新用户信息结果: " + updateResult);

        // 打印调试信息
        System.out.println("用户ID: " + userId + ", 资料ID: " + id + ", 资料上传者ID: " + material.getUserId());
        System.out.println("是否是自己的资料: " + isOwnMaterial);
        System.out.println("扣减积分: " + pointsDeducted);
        System.out.println("更新后积分: " + user.getPoints());
        System.out.println("更新后下载次数: " + user.getDownloadCount());

        // 增加下载次数
        materialService.incrementDownloadCount(id);

        // 创建下载记录
        DownloadRecord downloadRecord = new DownloadRecord();
        downloadRecord.setUserId(userId);
        downloadRecord.setMaterialId(id);
        downloadRecord.setPoints(pointsDeducted);
        downloadRecord.setDownloadTime(java.time.LocalDateTime.now());
        downloadRecordService.save(downloadRecord);

        // 读取并返回文件
        String userDir = System.getProperty("user.dir");
        System.out.println("当前工作目录: " + userDir);
        String filePath = userDir + File.separator + "upload" + File.separator
                + material.getFilePath();
        System.out.println("构建的文件路径: " + filePath);
        File file = new File(filePath);
        System.out.println("文件是否存在: " + file.exists());
        System.out.println("文件是否可读: " + file.canRead());
        if (!file.exists()) {
            try {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"code\":404,\"message\":\"文件不存在\",\"data\":null}");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        try {
            // 设置响应头
            response.setContentType("application/octet-stream");
            response.setContentLengthLong(file.length());
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode(material.getFileName(), "UTF-8"));

            // 读取文件并写入响应流
            FileInputStream fis = new FileInputStream(file);
            OutputStream os = response.getOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
            os.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"code\":500,\"message\":\"文件下载失败\",\"data\":null}");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}