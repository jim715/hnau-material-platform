package com.hnau.platform.common;

/**
 * 统一返回结果类
 */
public class Result {
    private int code;
    private String message;
    private Object data;
    
    // 默认构造方法，用于Jackson序列化
    public Result() {
    }
    
    private Result(int code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    /**
     * 成功返回
     */
    public static Result success(Object data) {
        return new Result(200, "success", data);
    }
    
    /**
     * 成功返回
     */
    public static Result success(String message) {
        return new Result(200, message, null);
    }
    
    /**
     * 成功返回
     */
    public static Result success(Object data, String message) {
        return new Result(200, message, data);
    }
    
    /**
     * 成功返回
     */
    public static Result success() {
        return new Result(200, "success", null);
    }
    
    /**
     * 失败返回
     */
    public static Result error(String message) {
        return new Result(500, message, null);
    }
    
    /**
     * 失败返回
     */
    public static Result error(int code, String message) {
        return new Result(code, message, null);
    }
    
    // getter and setter
    public int getCode() {
        return code;
    }
    
    public void setCode(int code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
}