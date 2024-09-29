package com.example.user_management;


import com.example.user_management.model.User;
import com.example.user_management.repository.UserRepository;
import com.example.user_management.service.EmailService;
import com.example.user_management.service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Optional;

class UserServiceTest {

    @InjectMocks
    private UserService userService;
    
    @Mock
    private EmailService emailService; 

    @Mock
    private UserRepository userRepository;
    
    private User user;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Smith");
        user.setEmail("john.smith@example.com");
        user.setActive(true);
    }

    @Test
    void testCreateUser() {
    	// Arrange
        when(userRepository.save(any(User.class))).thenReturn(user); // Mocking save method to return the user

        User createdUser = userService.createUser(user);
        // Assert
        assertEquals("john.smith@example.com", createdUser.getEmail()); // Verify the email is as expected
        verify(emailService).sendWelcomeEmail("john.smith@example.com", "John"); // Verify that the email was sent
    }
    
    @Test
    void testGetAllUsers() {
        // Arrange
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));

        // Act
        var users = userService.getAllUsers();

        // Assert
        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("John", users.get(0).getFirstName());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserById() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        var foundUser = userService.getUserById(1L);

        // Assert
        assertNotNull(foundUser);
        assertEquals("John", foundUser.getFirstName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserById_UserNotFound() {
        // Arrange
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> userService.getUserById(2L));
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(2L);
    }

    @Test
    void testUpdateUser() {
        // Arrange
        User updatedUser = new User();
        updatedUser.setFirstName("Jane");
        updatedUser.setLastName("Smith");
        updatedUser.setEmail("jane.smith@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        var result = userService.updateUser(1L, updatedUser);

        // Assert
        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        assertEquals("jane.smith@example.com", result.getEmail());
        verify(userRepository, times(1)).save(user);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteUser() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.deleteUser(1L);

        // Assert
        assertFalse(user.isActive());
        verify(userRepository, times(1)).save(user);
        verify(userRepository, times(1)).findById(1L);
    }
}

