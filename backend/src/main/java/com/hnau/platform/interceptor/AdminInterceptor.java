package com.hnau.platform.interceptor;

import com.hnau.platform.common.Result;
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
 * 管理员权限拦截器
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        System.out.println("=== 管理员拦截器请求开始 ===");
        System.out.println("请求路径: " + request.getRequestURI());

        // 从请求头获取token
        String token = request.getHeader("Authorization");
        System.out.println("请求头中的token: " + token);
        if (StringUtils.isBlank(token)) {
            System.out.println("未登录，token为空");
            sendErrorResponse(response, "未登录");
            return false;
        }

        // 移除Bearer前缀
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
            System.out.println("移除Bearer前缀后的token: " + token);
        }

        try {
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
                sendErrorResponse(response, "用户不存在");
                return false;
            }
            // 验证用户是否为管理员
            System.out.println("用户角色: " + user.getRole());
            if (user.getRole() == null || user.getRole() != 1) {
                System.out.println("无管理员权限");
                sendErrorResponse(response, "无管理员权限");
                return false;
            }
            // 将用户ID存储到请求中，方便后续使用
            request.setAttribute("userId", userId);
            System.out.println("=== 管理员拦截器请求通过 ===");
            return true;
        } catch (Exception e) {
            System.out.println("=== 管理员拦截器请求错误 ===");
            e.printStackTrace();
            sendErrorResponse(response, "无效的token");
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