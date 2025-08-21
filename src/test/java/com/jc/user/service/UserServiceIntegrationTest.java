package com.jc.user.service;

import com.jc.user.entity.User;
import com.jc.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application.yml")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void save() {
        // Given
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");


        // When
        User savedUser = userService.save(user);

        // Then
        assertThat(savedUser.getId()).isNotNull(); // 确保ID已生成
    }

    @Test
    void findAll() {
        List<User> users = userService.findAll();
        for (User user : users) {
            System.out.println(user.toString());
        }
    }
}