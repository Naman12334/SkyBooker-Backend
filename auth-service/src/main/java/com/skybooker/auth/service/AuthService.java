package com.skybooker.auth.service;

import com.skybooker.auth.entity.User;
import java.util.List;
import java.util.Map;

public interface AuthService {

    // Register new user
    User register(User user);

    // Login and return JWT token
    String login(String email, String password);

    // Logout
    void logout(String token);

    // Validate JWT token
    boolean validateToken(String token);

    // Refresh JWT token
    String refreshToken(String token);

    // Get user by ID
    User getUserById(int userId);

    // Get user by email
    User getUserByEmail(String email);

    // Update user profile
    User updateProfile(int userId, User user);

    // Change password
    void changePassword(int userId, Map<String, String> passwords);

    // Deactivate account
    void deactivateAccount(int userId);

    // Get all users (Admin only)
    List<User> getAllUsers();
}