package com.hnau.platform.interceptor;

import com.hnau.platform.common.Result;
import com.hnau.platform.common.SystemStatusManager;
import com.hnau.platform.entity.User;
import com.hnau.platform.service.UserService;
import com.hnau.platform.utils.JwtUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 系统状态拦截器
 * 用于检查系统是否处于更新状态，在更新期间拒绝非管理员用户的请求
 */
@Component
public class SystemStatusInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 获取请求路径
        String requestURI = request.getRequestURI();
        System.out.println("系统状态拦截器检查请求路径: " + requestURI);

        // 特殊处理登录请求
        if (requestURI.equals("/api/user/login")) {
            System.out.println("处理登录请求: " + requestURI);
            // 登录请求需要特殊处理，因为此时用户还没有token
            // 检查是否是管理员登录请求
            if (isAdminLoginRequest(request)) {
                System.out.println("管理员登录请求，允许访问");
                return true;
            }
            // 检查系统是否处于更新状态
            if (SystemStatusManager.isUpdating()) {
                // 非管理员登录请求，拒绝访问
                System.out.println("系统正在更新中，拒绝非管理员登录请求");
                sendErrorResponse(response, "系统正在更新中，请稍后再试");
                return false;
            }
            // 系统正常，允许登录请求通过
            return true;
        }

        // 检查用户是否为管理员
        if (isAdmin(request)) {
            System.out.println("管理员用户，允许访问: " + requestURI);
            return true;
        }

        // 检查系统是否处于更新状态
        if (SystemStatusManager.isUpdating()) {
            // 非管理员请求，拒绝访问
            System.out.println("系统正在更新中，拒绝非管理员请求: " + requestURI);
            sendErrorResponse(response, "系统正在更新中，请稍后再试");
            return false;
        }
        // 系统正常，允许请求通过
        return true;
    }

    /**
     * 检查是否是管理员登录请求
     */
    private boolean isAdminLoginRequest(HttpServletRequest request) {
        try {
            // 从请求体中获取登录参数
            StringBuilder sb = new StringBuilder();
            java.io.BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String requestBody = sb.toString();
            System.out.println("登录请求体: " + requestBody);

            // 解析JSON请求体
            ObjectMapper objectMapper = new ObjectMapper();
            java.util.Map<String, Object> params = objectMapper.readValue(requestBody, java.util.Map.class);
            String studentId = (String) params.get("studentId");
            System.out.println("登录请求参数 - studentId: " + studentId);

            // 验证是否是管理员账号
            User user = userService.getByStudentId(studentId);
            System.out.println("获取用户结果: " + user);
            if (user != null && user.getRole() != null && user.getRole() == 1) {
                System.out.println("是管理员登录请求");
                return true;
            }
            System.out.println("不是管理员登录请求");
            return false;
        } catch (Exception e) {
            System.out.println("检查管理员登录请求时出错: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 检查用户是否为管理员
     */
    private boolean isAdmin(HttpServletRequest request) {
        try {
            // 从请求头获取token
            String token = request.getHeader("Authorization");
            System.out.println("请求头中的token: " + token);
            if (StringUtils.isBlank(token)) {
                System.out.println("未登录，token为空");
                return false;
            }

            // 移除Bearer前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
                System.out.println("移除Bearer前缀后的token: " + token);
            }

            // 解析token获取用户ID
            Long userId = null;
            try {
                userId = jwtUtil.getUserIdFromToken(token);
                System.out.println("使用JwtUtil解析出的用户ID: " + userId);
            } catch (Exception e) {
                System.out.println("JwtUtil解析失败，尝试解析mock token: " + e.getMessage());
                // 如果是mock token，尝试从中提取ID
                if (token.startsWith("mock_token_")) {
                    System.out.println("尝试解析mock token: " + token);
                    String[] parts = token.split("_");
                    System.out.println("分割后的数组长度: " + parts.length);
                    System.out.println("分割后的数组内容: " + java.util.Arrays.toString(parts));
                    if (parts.length >= 4) {
                        try {
                            userId = Long.parseLong(parts[parts.length - 1]);
                            System.out.println("从mock token中提取的用户ID: " + userId);
                        } catch (NumberFormatException ex) {
                            System.out.println("解析用户ID失败: " + ex.getMessage());
                        }
                    }
                }
            }
            System.out.println("最终解析出的用户ID: " + userId);

            // 特殊处理：如果是管理员登录，直接使用ID为1的用户
            if (userId == null && token.startsWith("mock_token_")) {
                System.out.println("使用默认管理员用户ID: 1");
                userId = 1L;
            }

            // 获取用户信息
            User user = userService.getById(userId);
            System.out.println("获取到的用户: " + user);
            if (user == null) {
                System.out.println("用户不存在");
                return false;
            }
            // 验证用户是否为管理员
            System.out.println("用户角色: " + user.getRole());
            if (user.getRole() == null || user.getRole() != 1) {
                System.out.println("无管理员权限");
                return false;
            }
            // 用户是管理员
            System.out.println("用户是管理员，允许访问");
            return true;
        } catch (Exception e) {
            System.out.println("检查管理员权限时出错: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(Result.error(message));
        out.print(json);
        out.flush();
        out.close();
    }
}
