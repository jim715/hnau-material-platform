package com.hnau.platform.service;

import com.hnau.platform.entity.User;
import com.hnau.platform.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void testLogin() {
        try {
            String studentId = "admin";
            String password = "123456";
            System.out.println("Testing login with studentId: " + studentId + ", password: " + password);
            User user = userService.login(studentId, password);
            System.out.println("Login result: " + user);
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}