import com.hnau.platform.common.Result;
import java.util.HashMap;
import java.util.Map;

public class TestResult {
    public static void main(String[] args) {
        System.out.println("=== Testing Result class ===");
        
        // 测试Result.success()
        System.out.println("Testing Result.success()");
        Result successResult = Result.success("Success message");
        System.out.println("Success result: " + successResult);
        
        // 测试Result.success() with map
        System.out.println("\nTesting Result.success() with map");
        Map<String, Object> map = new HashMap<>();
        map.put("key", "value");
        Result successMapResult = Result.success(map);
        System.out.println("Success map result: " + successMapResult);
        
        // 测试Result.error()
        System.out.println("\nTesting Result.error()");
        Result errorResult = Result.error("Error message");
        System.out.println("Error result: " + errorResult);
        
        System.out.println("=== Testing Result class completed ===");
    }
}