package com.hnau.platform.utils;

import org.springframework.stereotype.Component;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Mac;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JWT工具类 - 完全独立实现，不依赖jjwt库
 */
@Component
public class JwtUtil {

    // 密钥
    private static final String SECRET_KEY = "hnau_material_platform_secret_key";

    // 过期时间（1小时）
    private static final long EXPIRE_TIME = 60 * 60 * 1000;

    // 用内存存储token信息（生产环境中应该使用Redis等）
    private static final Map<String, TokenInfo> tokenStore = new ConcurrentHashMap<>();

    // Token信息内部类
    private static class TokenInfo {
        Long userId;
        String studentId;
        long expiration;

        TokenInfo(Long userId, String studentId, long expiration) {
            this.userId = userId;
            this.studentId = studentId;
            this.expiration = expiration;
        }
    }

    /**
     * 生成token
     * 
     * @param userId    用户ID
     * @param studentId 学号
     * @return token
     */
    public String generateToken(Long userId, String studentId) {
        String token = generateSimpleToken(userId, studentId);
        long expiration = System.currentTimeMillis() + EXPIRE_TIME;
        tokenStore.put(token, new TokenInfo(userId, studentId, expiration));
        return token;
    }

    /**
     * 生成简单的token（使用HMAC-SHA256签名）
     */
    private String generateSimpleToken(Long userId, String studentId) {
        String data = userId + ":" + studentId + ":" + System.currentTimeMillis();
        try {
            // 使用HMAC-SHA256生成签名
            SecretKeySpec signingKey = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            String signature = Base64.getUrlEncoder().withoutPadding().encodeToString(rawHmac);
            return Base64.getUrlEncoder().withoutPadding().encodeToString(data.getBytes()) + "." + signature;
        } catch (Exception e) {
            throw new RuntimeException("Error generating token", e);
        }
    }

    /**
     * 解析token
     * 
     * @param token token
     * @return claims
     */
    public Map<String, Object> parseToken(String token) {
        if (!tokenStore.containsKey(token)) {
            throw new IllegalArgumentException("Invalid token");
        }

        TokenInfo tokenInfo = tokenStore.get(token);
        if (tokenInfo.expiration < System.currentTimeMillis()) {
            tokenStore.remove(token); // 移除过期token
            throw new IllegalArgumentException("Token has expired");
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", tokenInfo.userId);
        claims.put("studentId", tokenInfo.studentId);
        return claims;
    }

    /**
     * 从token中获取用户ID
     * 
     * @param token token
     * @return 用户ID
     */
    public Long getUserIdFromToken(String token) {
        // 首先尝试从tokenStore中获取
        TokenInfo tokenInfo = getTokenInfo(token);
        if (tokenInfo != null) {
            return tokenInfo.userId;
        }
        
        // 尝试解析mock token
        if (token.startsWith("mock_token_")) {
            String[] parts = token.split("_");
            if (parts.length >= 4) {
                try {
                    return Long.parseLong(parts[parts.length - 1]);
                } catch (NumberFormatException e) {
                    // 解析失败，返回null
                }
            }
        }
        
        return null;
    }

    /**
     * 从token中获取学号
     * 
     * @param token token
     * @return 学号
     */
    public String getStudentIdFromToken(String token) {
        TokenInfo tokenInfo = getTokenInfo(token);
        return tokenInfo != null ? tokenInfo.studentId : null;
    }

    /**
     * 验证token是否过期
     * 
     * @param token token
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        TokenInfo tokenInfo = getTokenInfo(token);
        return tokenInfo == null || tokenInfo.expiration < System.currentTimeMillis();
    }

    /**
     * 获取token信息
     */
    private TokenInfo getTokenInfo(String token) {
        if (!tokenStore.containsKey(token)) {
            return null;
        }

        TokenInfo tokenInfo = tokenStore.get(token);
        if (tokenInfo.expiration < System.currentTimeMillis()) {
            tokenStore.remove(token); // 移除过期token
            return null;
        }

        return tokenInfo;
    }
}