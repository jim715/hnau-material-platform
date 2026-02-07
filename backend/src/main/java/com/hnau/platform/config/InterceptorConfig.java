package com.hnau.platform.config;

import com.hnau.platform.interceptor.AdminInterceptor;
import com.hnau.platform.interceptor.SystemStatusInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置类
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    private AdminInterceptor adminInterceptor;
    
    @Autowired
    private SystemStatusInterceptor systemStatusInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册系统状态拦截器，拦截所有路径（除了登录接口）
        registry.addInterceptor(systemStatusInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/user/login"); // 登录接口不需要拦截
        
        // 注册管理员权限拦截器，拦截所有/admin路径
        registry.addInterceptor(adminInterceptor)
                .addPathPatterns("/api/admin/**")
                .excludePathPatterns("/api/admin/login"); // 登录接口不需要拦截
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 静态资源映射，将/upload目录映射为/static路径
        registry.addResourceHandler("/static/**")
                .addResourceLocations("file:" + System.getProperty("user.dir") + "/upload/");
    }
}