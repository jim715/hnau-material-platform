import com.hnau.platform.service.impl.UserServiceImpl;
import com.hnau.platform.entity.User;

public class TestUserServiceImpl {
    public static void main(String[] args) {
        try {
            // 创建UserServiceImpl实例
            UserServiceImpl userService = new UserServiceImpl();
            
            // 测试login方法
            System.out.println("Testing login method...");
            User user = userService.login("admin", "123456");
            
            if (user != null) {
                System.out.println("Login successful! User: " + user);
            } else {
                System.out.println("Login failed: User not found or password incorrect");
            }
        } catch (Exception e) {
            System.out.println("Error during login test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}