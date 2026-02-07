package com.hnau.platform.controller;

import com.hnau.platform.common.Result;
import com.hnau.platform.common.SystemStatusManager;
import com.hnau.platform.entity.Material;
import com.hnau.platform.entity.User;
import com.hnau.platform.service.DownloadRecordService;
import com.hnau.platform.service.UploadRecordService;
import com.hnau.platform.service.IntegralService;
import com.hnau.platform.service.MaterialService;
import com.hnau.platform.service.SearchService;
import com.hnau.platform.service.UserService;
import com.hnau.platform.service.LoginRecordService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private MaterialService materialService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private IntegralService integralService;

    @Autowired
    private UploadRecordService uploadRecordService;

    @Autowired
    private DownloadRecordService downloadRecordService;

    @Autowired
    private LoginRecordService loginRecordService;

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result login(@RequestParam String studentId, @RequestParam String password) {
        // 验证用户名和密码
        User user = userService.login(studentId, password);
        if (user != null && user.getRole() != null && user.getRole() == 1) {
            // 生成token并返回
            return Result.success(user);
        }
        return Result.error("学号或密码错误，或无管理员权限");
    }

    /**
     * 获取待审核资料列表
     */
    @GetMapping("/material/audit/list")
    public Result getAuditList() {
        List<Material> materials = materialService.getAuditList();
        return Result.success(materials);
    }

    /**
     * 审核资料
     */
    @PostMapping("/material/audit")
    public Result auditMaterial(@RequestBody java.util.Map<String, Object> params) {
        Long materialId = ((Number) params.get("materialId")).longValue();
        Integer auditStatus = ((Number) params.get("auditStatus")).intValue();
        boolean success = materialService.auditMaterial(materialId, auditStatus);
        if (success) {
            return Result.success("审核成功");
        }
        return Result.error("审核失败");
    }

    /**
     * 违规处理 - 删除违规资料
     */
    @DeleteMapping("/violate/handle/material/{id}")
    public Result deleteViolateMaterial(@PathVariable Long id) {
        boolean success = materialService.removeById(id);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    /**
     * 违规处理 - 扣减用户积分
     */
    @PostMapping("/violate/handle/points")
    public Result deductUserPoints(@RequestParam Long userId, @RequestParam Integer points) {
        // 验证积分范围
        if (points < 20 || points > 50) {
            return Result.error("积分扣减范围应在20-50之间");
        }
        boolean success = userService.deductPoints(userId, points);
        if (success) {
            return Result.success("积分扣减成功");
        }
        return Result.error("积分扣减失败");
    }

    /**
     * 热搜管理 - 屏蔽关键词
     */
    @PostMapping("/hot/shield")
    public Result shieldHotKeyword(@RequestParam String keyword) {
        boolean success = searchService.shieldKeyword(keyword);
        if (success) {
            return Result.success("关键词屏蔽成功");
        }
        return Result.error("关键词屏蔽失败");
    }

    /**
     * 热搜管理 - 解除屏蔽关键词
     */
    @PostMapping("/hot/unshield")
    public Result unshieldHotKeyword(@RequestParam String keyword) {
        boolean success = searchService.unshieldKeyword(keyword);
        if (success) {
            return Result.success("关键词解除屏蔽成功");
        }
        return Result.error("关键词解除屏蔽失败");
    }

    /**
     * 农科精选 - 标记优质农科资料
     */
    @PostMapping("/material/agriTag")
    public Result markAgriMaterial(@RequestParam Long materialId, @RequestParam Integer agriTag) {
        boolean success = materialService.markAgriMaterial(materialId, agriTag);
        if (success) {
            return Result.success("标记成功");
        }
        return Result.error("标记失败");
    }

    /**
     * 批量上传学生信息
     */
    @PostMapping("/user/batch")
    public Result batchCreateUsers(@RequestBody List<User> users) {
        boolean success = userService.batchCreateUsers(users);
        if (success) {
            return Result.success("批量上传成功");
        }
        return Result.error("批量上传失败");
    }

    /**
     * 获取用户列表（包含积分信息）
     */
    @GetMapping("/user/list")
    public Result getUserList() {
        System.out.println("=== 获取用户列表请求开始 ===");
        try {
            List<User> users = userService.list();
            System.out.println("获取到用户数量: " + users.size());

            // 构建只包含关键字段的用户信息列表
            List<java.util.Map<String, Object>> userInfoList = new java.util.ArrayList<>();
            for (User user : users) {
                System.out.println("处理用户: " + user.getId() + " - " + user.getName());
                Integer totalPoints = integralService.getUserTotalPoints(user.getId());
                System.out.println("用户积分: " + totalPoints);

                // 只包含关键字段
                java.util.Map<String, Object> userInfo = new java.util.HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("studentId", user.getStudentId());
                userInfo.put("name", user.getName());
                userInfo.put("grade", user.getGrade());
                userInfo.put("major", user.getMajor());
                userInfo.put("college", user.getCollege());
                userInfo.put("className", user.getClassName());
                userInfo.put("points", totalPoints);

                userInfoList.add(userInfo);
            }

            System.out.println("=== 获取用户列表请求结束 ===");
            return Result.success(userInfoList);
        } catch (Exception e) {
            System.out.println("=== 获取用户列表请求错误 ===");
            e.printStackTrace();
            return Result.error("获取用户列表失败: " + e.getMessage());
        }
    }

    /**
     * 修改用户积分
     */
    @PostMapping("/integral/update")
    public Result updateUserPoints(@RequestBody java.util.Map<String, Object> params) {
        Long userId = ((Number) params.get("userId")).longValue();
        Integer points = ((Number) params.get("points")).intValue();
        String reason = (String) params.get("reason");

        boolean success = integralService.updateIntegral(userId, points, reason, null, 5); // 类型5表示管理员操作
        if (success) {
            return Result.success("积分修改成功");
        }
        return Result.error("积分修改失败");
    }

    /**
     * 清除用户积分及相关数据
     */
    @PostMapping("/integral/clear")
    public Result clearUserPoints(@RequestBody java.util.Map<String, Object> params) {
        Long userId = ((Number) params.get("userId")).longValue();

        try {
            // 清除积分记录
            integralService.clearUserPoints(userId);
            // 清除上传历史
            uploadRecordService.clearUserUploadRecords(userId);
            // 清除下载历史
            downloadRecordService.clearUserDownloadRecords(userId);
            // 更新用户的上传和下载次数为0，积分也设置为0
            User user = userService.getById(userId);
            if (user != null) {
                user.setUploadCount(0);
                user.setDownloadCount(0);
                user.setPoints(0);
                userService.updateById(user);
            }
            return Result.success("积分清除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("积分清除失败");
        }
    }

    /**
     * 测试接口，不需要认证
     */
    @GetMapping("/test")
    public Result test() {
        System.out.println("=== 测试接口被调用 ===");
        // 返回一个包含复杂对象的Result对象，测试JSON序列化
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("message", "测试接口调用成功");
        data.put("timestamp", System.currentTimeMillis());
        data.put("status", "success");
        return Result.success(data);
    }

    /**
     * 重置用户密码
     */
    @PostMapping("/user/resetPassword")
    public Result resetUserPassword(@RequestBody java.util.Map<String, Object> params) {
        System.out.println("=== 重置用户密码请求开始 ===");
        try {
            Long userId = ((Number) params.get("userId")).longValue();
            String defaultPassword = "123456";

            // 获取用户
            User user = userService.getById(userId);
            if (user == null) {
                System.out.println("用户不存在: " + userId);
                return Result.error("用户不存在");
            }

            // 使用默认密码123456的MD5哈希值
            String defaultPasswordMd5 = "e10adc3949ba59abbe56e057f20f883e";
            user.setPassword(defaultPasswordMd5);

            // 保存更新
            boolean success = userService.updateById(user);
            System.out.println("密码重置结果: " + success);

            if (success) {
                System.out.println("=== 重置用户密码请求结束 ===");
                return Result.success("密码重置成功，默认密码为: 123456");
            } else {
                System.out.println("=== 重置用户密码请求结束 ===");
                return Result.error("密码重置失败");
            }
        } catch (Exception e) {
            System.out.println("=== 重置用户密码请求错误 ===");
            e.printStackTrace();
            return Result.error("密码重置失败: " + e.getMessage());
        }
    }

    /**
     * 获取登录记录列表
     */
    @GetMapping("/login/records")
    public Result getLoginRecords(@RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        System.out.println("=== 获取登录记录请求开始 ===");
        try {
            System.out.println("页码: " + page + ", 每页大小: " + pageSize);
            java.util.Map<String, Object> result = loginRecordService.getLoginRecords(page, pageSize);
            System.out.println("获取登录记录成功，记录数: " + ((java.util.List<?>) result.get("list")).size());
            System.out.println("=== 获取登录记录请求结束 ===");
            return Result.success(result);
        } catch (Exception e) {
            System.out.println("=== 获取登录记录请求错误 ===");
            e.printStackTrace();
            return Result.error("获取登录记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定用户的登录记录
     */
    @GetMapping("/login/records/{userId}")
    public Result getLoginRecordsByUserId(@PathVariable Long userId, @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        System.out.println("=== 获取用户登录记录请求开始 ===");
        try {
            System.out.println("用户ID: " + userId + ", 页码: " + page + ", 每页大小: " + pageSize);
            java.util.Map<String, Object> result = loginRecordService.getLoginRecordsByUserId(userId, page, pageSize);
            System.out.println("获取用户登录记录成功，记录数: " + ((java.util.List<?>) result.get("list")).size());
            System.out.println("=== 获取用户登录记录请求结束 ===");
            return Result.success(result);
        } catch (Exception e) {
            System.out.println("=== 获取用户登录记录请求错误 ===");
            e.printStackTrace();
            return Result.error("获取用户登录记录失败: " + e.getMessage());
        }
    }

    /**
     * 系统更新开始，强制所有用户退出
     */
    @PostMapping("/system/update/start")
    public Result startSystemUpdate(@RequestBody java.util.Map<String, Object> params) {
        System.out.println("=== 系统更新开始请求 ===");
        try {
            // 解析参数
            System.out.println("更新参数: " + params);

            // 设置系统为更新状态
            SystemStatusManager.setUpdating(true);
            System.out.println("设置系统为更新状态");

            // 强制所有用户退出
            System.out.println("开始强制所有用户退出...");
            // 调用LoginRecordService的方法来更新所有在线用户的状态
            boolean success = loginRecordService.forceAllUsersLogout();

            if (success) {
                System.out.println("系统更新开始成功，所有用户已被强制退出");
                return Result.success("系统更新开始成功，所有用户已被强制退出");
            } else {
                // 如果强制退出失败，恢复系统状态
                SystemStatusManager.setUpdating(false);
                System.out.println("系统更新开始失败，恢复系统状态");
                return Result.error("系统更新开始失败");
            }
        } catch (Exception e) {
            System.out.println("系统更新开始错误: " + e.getMessage());
            e.printStackTrace();
            return Result.error("系统更新开始失败: " + e.getMessage());
        }
    }

    /**
     * 系统更新完成，恢复正常功能
     */
    @PostMapping("/system/update/complete")
    public Result completeSystemUpdate() {
        System.out.println("=== 系统更新完成请求 ===");
        try {
            // 恢复系统正常状态
            System.out.println("系统更新完成，恢复正常功能...");
            // 设置系统为非更新状态
            SystemStatusManager.setUpdating(false);
            System.out.println("设置系统为非更新状态");

            System.out.println("系统更新完成成功");
            return Result.success("系统更新完成，所有功能已恢复正常");
        } catch (Exception e) {
            System.out.println("系统更新完成错误: " + e.getMessage());
            e.printStackTrace();
            return Result.error("系统更新完成失败: " + e.getMessage());
        }
    }

    /**
     * MD5加密方法
     */
    private String getMD5(String input) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}