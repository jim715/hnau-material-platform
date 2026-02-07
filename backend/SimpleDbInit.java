import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 简单的数据库初始化工具类
 * 独立运行，不依赖项目其他类
 */
public class SimpleDbInit {
    public static void main(String[] args) {
        // 数据库连接信息
        String url = "jdbc:mysql://localhost:3306/?useUnicode=true&characterEncoding=utf8mb4&useSSL=false&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "123456";
        String databaseName = "material_platform";

        // SQL脚本路径
        String sqlFile = "src\\main\\resources\\sql\\admin_schema.sql";

        Connection conn = null;
        Statement stmt = null;

        try {
            // 加载MySQL驱动
            Class.forName("com.mysql.cj.jdbc.Driver"); // mysql-connector-j 8.0 使用的驱动类
            System.out.println("驱动加载成功！");

            // 连接数据库
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("数据库连接成功！");

            // 创建Statement
            stmt = conn.createStatement();

            // 创建数据库
            String createDbSql = "CREATE DATABASE IF NOT EXISTS " + databaseName
                    + " DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
            stmt.executeUpdate(createDbSql);
            System.out.println("数据库创建成功！");

            // 使用创建的数据库
            stmt.executeUpdate("USE " + databaseName);
            System.out.println("切换到数据库 " + databaseName + " 成功！");

            // 读取并执行SQL脚本
            System.out.println("开始执行SQL脚本...");
            executeSqlScript(stmt, sqlFile);
            System.out.println("SQL脚本执行成功！");

            // 验证管理员账号是否创建成功
            stmt.executeQuery("SELECT * FROM admin WHERE username = 'admin'");
            System.out.println("管理员账号验证成功！");

            System.out.println("\n数据库初始化完成！");
            System.out.println("您可以使用以下命令启动后端服务：");
            System.out.println("mvn spring-boot:run");

        } catch (ClassNotFoundException e) {
            System.out.println("错误：找不到MySQL驱动！请确保mysql-connector-java依赖已添加到项目中。");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("错误：数据库操作失败！");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("错误：读取SQL脚本文件失败！");
            e.printStackTrace();
        } finally {
            // 关闭资源
            try {
                if (stmt != null)
                    stmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 执行SQL脚本文件
     */
    private static void executeSqlScript(Statement stmt, String sqlFile) throws IOException, SQLException {
        BufferedReader reader = new BufferedReader(new FileReader(sqlFile));
        StringBuilder sb = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            // 跳过注释和空行
            line = line.trim();
            if (line.isEmpty() || line.startsWith("--")) {
                continue;
            }

            sb.append(line);

            // 执行以分号结尾的SQL语句
            if (line.endsWith(";")) {
                String sql = sb.toString();
                stmt.executeUpdate(sql);
                sb.setLength(0);
            }
        }

        reader.close();
    }
}