package com.jc.user.service;

import com.jc.user.entity.User;
import com.jc.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


/**
 * @author jinchenj
 * @description
 * @create:2025-08-2116:53:28
 */
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save() {
        // Given
        User user = new User();
        user.setName("Test User");

        // When
        User savedUser = userService.save(user);

        // Then
        assertNotNull(savedUser);
        assertEquals("Test User", savedUser.getName());
    }

    /*
    @Test
    void deleteById_shouldCallRepositoryDeleteById() {
        // Given
        Long userId = 1L;

        // When
        userService.deleteById(userId);

        // Then
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void findAll_shouldReturnAllUsers() {
        // Given
        List<User> users = new ArrayList<>();
        User user1 = new User();
        user1.setId(1L);
        user1.setName("User 1");
        User user2 = new User();
        user2.setId(2L);
        user2.setName("User 2");
        users.add(user1);
        users.add(user2);

        when(userRepository.findAll()).thenReturn(users);

        // When
        List<User> result = userService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("User 1", result.get(0).getName());
        assertEquals("User 2", result.get(1).getName());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findById_whenUserExists_shouldReturnUser() {
        // Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("Test User");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        User result = userService.findById(userId);

        // Then
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Test User", result.getName());
    }

    @Test
    void findById_whenUserNotExists_shouldReturnNull() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        User result = userService.findById(userId);

        // Then
        assertNull(result);
    }

    @Test
    void findByName_shouldReturnUsersWithMatchingName() {
        // Given
        String name = "Test User";
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setId(1L);
        user.setName(name);
        users.add(user);

        when(userRepository.findByName(name)).thenReturn(users);

        // When
        List<User> result = userService.findByName(name);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(name, result.get(0).getName());
        verify(userRepository, times(1)).findByName(name);
    }
    */
}