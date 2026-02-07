import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestLoginApi {
    public static void main(String[] args) {
        try {
            // 创建URL对象 - 使用正确的端口和路径
            URL url = new URL("http://localhost:8082/api/user/login");
            
            // 创建HttpURLConnection对象
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            // 设置请求方法为POST
            conn.setRequestMethod("POST");
            
            // 设置请求头
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            
            // 允许发送和接收数据
            conn.setDoOutput(true);
            conn.setDoInput(true);
            
            // 创建请求体
            String jsonInputString = "{\"studentId\":\"admin\",\"password\":\"123456\"}";
            
            // 发送请求
            try (DataOutputStream dos = new DataOutputStream(conn.getOutputStream())) {
                dos.writeBytes(jsonInputString);
                dos.flush();
            }
            
            // 获取响应码
            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);
            
            // 获取响应内容
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    responseCode >= 400 ? conn.getErrorStream() : conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            // 打印响应内容
            System.out.println("Response Content: " + response.toString());
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}