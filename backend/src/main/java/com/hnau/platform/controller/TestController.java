package com.hnau.platform.controller;

import com.hnau.platform.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 */
@RestController
@RequestMapping("/api/test")
public class TestController {

    /**
     * 测试JSON序列化
     */
    @GetMapping("/json")
    public Map<String, Object> testJson() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "测试JSON序列化成功");
        result.put("data", null);
        return result;
    }

    /**
     * 测试字符串返回
     */
    @GetMapping("/string")
    public String testString() {
        return "测试字符串返回成功";
    }

    /**
     * 测试Result类序列化
     */
    @GetMapping("/result")
    public Result testResult() {
        return Result.success("测试Result类序列化成功");
    }
}
