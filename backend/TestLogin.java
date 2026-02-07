import com.hnau.platform.Application;
import com.hnau.platform.service.UserService;
import com.hnau.platform.entity.User;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

public class TestLogin {
    public static void main(String[] args) {
        try {
            // 启动Spring Boot应用
            ApplicationContext context = SpringApplication.run(Application.class, args);
            
            // 获取UserService实例
            UserService userService = context.getBean(UserService.class);
            
            // 测试login方法
            System.out.println("Testing login method...");
            String studentId = "admin";
            String password = "123456";
            System.out.println("Student ID: " + studentId);
            System.out.println("Password: " + password);
            
            User user = userService.login(studentId, password);
            System.out.println("Login result: " + user);
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}