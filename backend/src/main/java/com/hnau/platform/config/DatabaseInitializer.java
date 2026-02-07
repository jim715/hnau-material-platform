package com.hnau.platform.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 数据库初始化类
 * 在应用启动时执行数据库初始化脚本
 */
@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("开始执行数据库初始化脚本...");
        try {
            executeSqlScript("sql/full_schema.sql");
            System.out.println("数据库初始化脚本执行成功！");
        } catch (Exception e) {
            System.err.println("数据库初始化脚本执行失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 执行 SQL 脚本
     * 
     * @param scriptPath SQL 脚本路径
     * @throws Exception 执行异常
     */
    private void executeSqlScript(String scriptPath) throws Exception {
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        getClass().getClassLoader().getResourceAsStream(scriptPath), "UTF-8"))) {

            StringBuilder sqlBuilder = new StringBuilder();
            String line;

            // 先删除所有表，确保重新创建正确的表结构
            try {
                statement.execute("DROP TABLE IF EXISTS `admin`;");
                statement.execute("DROP TABLE IF EXISTS `shield_keyword`;");
                statement.execute("DROP TABLE IF EXISTS `material`;");
                statement.execute("DROP TABLE IF EXISTS `category`;");
                statement.execute("DROP TABLE IF EXISTS `user`;");
                System.out.println("已删除现有表结构，准备重新创建");
            } catch (SQLException e) {
                System.err.println("删除表失败：" + e.getMessage());
            }

            while ((line = reader.readLine()) != null) {
                // 跳过注释和空行
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--")) {
                    continue;
                }

                sqlBuilder.append(line);

                // 如果遇到分号，执行 SQL 语句
                if (line.endsWith(";")) {
                    String sql = sqlBuilder.toString();
                    try {
                        statement.execute(sql);
                    } catch (SQLException e) {
                        System.err.println("执行 SQL 语句失败：" + sql);
                        System.err.println("错误信息：" + e.getMessage());
                        // 继续执行其他语句，不中断整个脚本
                    }
                    sqlBuilder.setLength(0);
                }
            }
        } catch (IOException e) {
            System.err.println("读取 SQL 脚本文件失败：" + e.getMessage());
            throw e;
        } catch (SQLException e) {
            System.err.println("执行 SQL 语句失败：" + e.getMessage());
            throw e;
        }
    }
}
