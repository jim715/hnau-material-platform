import com.hnau.platform.service.impl.UserServiceImpl;

public class TestUserService {
    public static void main(String[] args) {
        try {
            UserServiceImpl userService = new UserServiceImpl();
            
            // 测试login方法
            System.out.println("Testing login method...");
            com.hnau.platform.entity.User user = userService.login("admin", "123456");
            System.out.println("Login result: " + user);
            
        } catch (Exception e) {
            System.out.println("Error occurred:");
            e.printStackTrace();
        }
    }
}