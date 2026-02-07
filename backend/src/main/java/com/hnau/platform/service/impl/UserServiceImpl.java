package com.hnau.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnau.platform.entity.User;
import com.hnau.platform.mapper.UserMapper;
import com.hnau.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User getByStudentId(String studentId) {
        return ((UserMapper) baseMapper).selectByStudentId(studentId);
    }

    @Override
    public User login(String studentId, String password) {
        try {
            // 检查参数
            if (studentId == null || studentId.isEmpty()) {
                return null;
            }
            if (password == null || password.isEmpty()) {
                return null;
            }

            // 获取用户
            User user = getByStudentId(studentId);

            if (user != null) {
                // 计算密码的MD5值
                String md5Password = getMD5(password);

                if (user.getPassword().equals(md5Password)) {
                    // 清除密码信息，避免返回给前端
                    user.setPassword(null);
                    return user;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 辅助方法：比较两个字符串是否相等，或者都为null
     */
    private boolean equalOrBothNull(String s1, String s2) {
        if (s1 == null && s2 == null) {
            return true;
        }
        if (s1 == null || s2 == null) {
            return false;
        }
        return s1.equals(s2);
    }

    /**
     * MD5加密方法
     */
    private String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean register(User user) {
        // 检查学号是否已存在
        if (getByStudentId(user.getStudentId()) != null) {
            return false;
        }
        // 设置初始积分
        user.setPoints(0);
        // 设置默认头像
        user.setAvatar("default.jpg");
        // 设置用户状态
        user.setStatus(1);
        return save(user);
    }

    @Override
    public boolean updatePoints(Long userId, int points) {
        User user = getById(userId);
        if (user != null) {
            user.setPoints(user.getPoints() + points);
            return updateById(user);
        }
        return false;
    }

    @Override
    public boolean deductPoints(Long userId, int points) {
        User user = getById(userId);
        if (user != null) {
            // 确保积分不会扣成负数
            int newPoints = Math.max(0, user.getPoints() - points);
            user.setPoints(newPoints);
            return updateById(user);
        }
        return false;
    }

    @Override
    public boolean batchCreateUsers(List<User> users) {
        try {
            System.out.println("=== 开始批量处理用户 ===");
            System.out.println("原始用户列表大小: " + users.size());
            System.out.println("原始用户列表: " + users);

            // 过滤无效用户
            List<User> validUsers = new ArrayList<>();
            for (User user : users) {
                if (isValidUser(user)) {
                    validUsers.add(user);
                }
            }

            System.out.println("过滤后有效用户数量: " + validUsers.size());
            System.out.println("有效用户列表: " + validUsers);

            List<User> newUsers = new ArrayList<>();
            int processedCount = 0;

            for (User user : validUsers) {
                try {
                    processedCount++;
                    System.out.println("处理第 " + processedCount + " 个用户: " + user.getStudentId());

                    // 检查学号是否已存在
                    User existingUser = getByStudentId(user.getStudentId());
                    if (existingUser != null) {
                        // 如果用户已存在，将Excel中的信息与数据库中的信息进行对比
                        try {
                            // 检查用户信息是否完全匹配
                            boolean allMatch = true;

                            // 详细日志：比较所有字段
                            System.out.println("=== 用户信息比较 ===");
                            System.out.println("学号: " + user.getStudentId());
                            System.out
                                    .println("现有姓名: '" + existingUser.getName() + "' vs 新姓名: '" + user.getName() + "'");
                            System.out.println(
                                    "现有年级: '" + existingUser.getGrade() + "' vs 新年级: '" + user.getGrade() + "'");
                            System.out.println(
                                    "现有专业: '" + existingUser.getMajor() + "' vs 新专业: '" + user.getMajor() + "'");
                            System.out.println(
                                    "现有学院: '" + existingUser.getCollege() + "' vs 新学院: '" + user.getCollege() + "'");
                            System.out.println("现有班级: '" + existingUser.getClassName() + "' vs 新班级: '"
                                    + user.getClassName() + "'");

                            // 比较所有关键字段
                            if (!equalOrBothNull(existingUser.getName(), user.getName())) {
                                allMatch = false;
                                existingUser.setName(user.getName());
                                System.out.println("姓名不匹配，更新为: " + user.getName());
                            }
                            if (!equalOrBothNull(existingUser.getGrade(), user.getGrade())) {
                                allMatch = false;
                                existingUser.setGrade(user.getGrade());
                                System.out.println("年级不匹配，更新为: " + user.getGrade());
                            }
                            if (!equalOrBothNull(existingUser.getMajor(), user.getMajor())) {
                                allMatch = false;
                                existingUser.setMajor(user.getMajor());
                                System.out.println("专业不匹配，更新为: " + user.getMajor());
                            }
                            if (!equalOrBothNull(existingUser.getCollege(), user.getCollege())) {
                                allMatch = false;
                                existingUser.setCollege(user.getCollege());
                                System.out.println("学院不匹配，更新为: " + user.getCollege());
                            }
                            if (!equalOrBothNull(existingUser.getClassName(), user.getClassName())) {
                                allMatch = false;
                                existingUser.setClassName(user.getClassName());
                                System.out.println("班级不匹配，更新为: " + user.getClassName());
                            }
                            if (!equalOrBothNull(existingUser.getStudentId(), user.getStudentId())) {
                                allMatch = false;
                                existingUser.setStudentId(user.getStudentId());
                                System.out.println("学号不匹配，更新为: " + user.getStudentId());
                            }

                            System.out.println("所有信息是否完全匹配: " + allMatch);

                            // 根据匹配结果处理密码
                            if (allMatch) {
                                // 所有信息都匹配，保持原有密码不变
                                System.out.println("用户信息完全匹配，保持原有密码不变: " + user.getStudentId());
                            } else {
                                // 有信息不匹配，重置密码为默认密码123456
                                String defaultPassword = "123456";
                                String md5Password = getMD5(defaultPassword);
                                existingUser.setPassword(md5Password);
                                System.out.println("用户信息有变动，重置密码为默认密码: " + user.getStudentId());
                            }

                            // 保存更新
                            updateById(existingUser);
                            System.out.println("用户信息更新完成: " + user.getStudentId());
                        } catch (Exception e) {
                            // 捕获更新用户信息异常，继续处理其他用户
                            System.err.println("更新用户信息失败: " + user.getStudentId());
                            e.printStackTrace();
                        }
                        continue;
                    }
                    // 设置初始密码（默认为123456，需要加密）
                    String defaultPassword = "123456";
                    String md5Password = getMD5(defaultPassword);
                    user.setPassword(md5Password);
                    // 设置初始积分
                    user.setPoints(0);
                    // 设置上传和下载次数
                    user.setUploadCount(0);
                    user.setDownloadCount(0);
                    // 设置用户状态
                    user.setStatus(1);
                    // 设置用户角色
                    user.setRole(0);
                    // 清除可能不存在的字段
                    user.setAvatar(null);
                    // 注意：由于数据库中可能没有这些字段，所以这里不设置这些字段
                    // 而是直接添加到新用户列表，让数据库自动处理这些字段
                    newUsers.add(user);
                } catch (Exception e) {
                    // 捕获单个用户处理异常，继续处理其他用户
                    System.err.println("处理用户失败: " + user.getStudentId());
                    e.printStackTrace();
                }
            }

            System.out.println("=== 批量处理用户完成 ===");
            System.out.println("总处理用户数: " + processedCount);
            System.out.println("新增用户数: " + newUsers.size());

            // 如果没有新用户需要创建，返回true，表示上传成功
            if (newUsers.isEmpty()) {
                System.out.println("没有新用户需要创建，返回成功");
                return true;
            }
            try {
                boolean saved = saveBatch(newUsers);
                System.out.println("批量保存新用户结果: " + saved);
                return saved;
            } catch (Exception e) {
                // 捕获批量保存异常，返回false，表示上传失败
                System.err.println("批量保存用户失败:");
                e.printStackTrace();
                return false;
            }
        } catch (Exception e) {
            // 捕获异常，返回false，表示上传失败
            System.err.println("批量创建用户失败:");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 验证用户数据是否有效
     */
    private boolean isValidUser(User user) {
        if (user == null) {
            System.out.println("过滤无效用户: null");
            return false;
        }

        // 检查学号
        String studentId = user.getStudentId();
        if (studentId == null || studentId.trim().isEmpty()) {
            System.out.println("过滤无效用户: 学号为空");
            return false;
        }

        // 检查学号格式（必须是数字）
        if (!studentId.matches("\\d+")) {
            System.out.println("过滤无效用户: 学号格式不正确: " + studentId);
            return false;
        }

        // 检查姓名
        String name = user.getName();
        if (name == null || name.trim().isEmpty()) {
            System.out.println("过滤无效用户: 姓名为空，学号: " + studentId);
            return false;
        }

        System.out.println("验证通过用户: " + studentId + " - " + name);
        return true;
    }
}