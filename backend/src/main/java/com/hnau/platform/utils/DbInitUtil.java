package com.hnau.platform.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 数据库初始化工具类
 * 用于执行SQL脚本初始化数据库结构
 */
public class DbInitUtil {
    
    // 数据库连接信息
    private static final String URL = "jdbc:mysql://localhost:3306/?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";
    
    // SQL脚本路径
    private static final String SQL_SCRIPT_PATH = "src/main/resources/sql/full_schema.sql";
    
    public static void main(String[] args) {
        System.out.println("开始初始化数据库...");
        
        try {
            // 加载驱动
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("数据库驱动加载成功");
            
            // 建立连接
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("数据库连接成功");
            
            // 创建数据库（如果不存在）
            createDatabase(connection);
            
            // 使用数据库
            connection.setCatalog("material_platform");
            System.out.println("切换到material_platform数据库");
            
            // 执行SQL脚本
            executeSqlScript(connection, SQL_SCRIPT_PATH);
            
            // 关闭连接
            connection.close();
            System.out.println("数据库初始化完成");
            
        } catch (ClassNotFoundException e) {
            System.err.println("数据库驱动加载失败: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("数据库操作失败: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("读取SQL文件失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建数据库（如果不存在）
     */
    private static void createDatabase(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        String sql = "CREATE DATABASE IF NOT EXISTS material_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
        statement.executeUpdate(sql);
        System.out.println("数据库创建成功（如果不存在）");
        statement.close();
    }
    
    /**
     * 执行SQL脚本
     */
    private static void executeSqlScript(Connection connection, String filePath) throws SQLException, IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        StringBuilder sqlBuilder = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            // 跳过注释行
            if (line.trim().startsWith("--")) {
                continue;
            }
            // 跳过空行
            if (line.trim().isEmpty()) {
                continue;
            }
            
            sqlBuilder.append(line);
            
            // 如果遇到分号，执行SQL语句
            if (line.trim().endsWith(";")) {
                String sql = sqlBuilder.toString();
                Statement statement = connection.createStatement();
                statement.executeUpdate(sql);
                System.out.println("执行SQL: " + sql.substring(0, Math.min(sql.length(), 50)) + (sql.length() > 50 ? "..." : ""));
                statement.close();
                sqlBuilder.setLength(0);
            }
        }
        
        reader.close();
        System.out.println("SQL脚本执行完成");
    }
}