package com.skybooker.auth.resource;

import com.skybooker.auth.entity.User;
import com.skybooker.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthResource {

    @Autowired
    private AuthService authService;

    // POST /auth/register
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        User registered = authService.register(user);
        return ResponseEntity.ok(registered);
    }

    // POST /auth/login
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        String token = authService.login(email, password);
        return ResponseEntity.ok(token);
    }

    // POST /auth/logout
    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok("Logged out successfully!");
    }

    // GET /auth/refresh
    @GetMapping("/refresh")
    public ResponseEntity<String> refresh(
            @RequestHeader("Authorization") String token) {
        String newToken = authService.refreshToken(token);
        return ResponseEntity.ok(newToken);
    }

    // GET /auth/profile/{userId}
    @GetMapping("/profile/{userId}")
    public ResponseEntity<User> getProfile(@PathVariable int userId) {
        User user = authService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    // PUT /auth/profile/{userId}
    @PutMapping("/profile/{userId}")
    public ResponseEntity<User> updateProfile(
            @PathVariable int userId,
            @RequestBody User user) {
        User updated = authService.updateProfile(userId, user);
        return ResponseEntity.ok(updated);
    }

    // PUT /auth/password/{userId}
    @PutMapping("/password/{userId}")
    public ResponseEntity<String> changePassword(
            @PathVariable int userId,
            @RequestBody Map<String, String> passwords) {
        authService.changePassword(userId, passwords);
        return ResponseEntity.ok("Password changed successfully!");
    }

    // PUT /auth/deactivate/{userId}
    @PutMapping("/deactivate/{userId}")
    public ResponseEntity<String> deactivateAccount(
            @PathVariable int userId) {
        authService.deactivateAccount(userId);
        return ResponseEntity.ok("Account deactivated successfully!");
    }

    // GET /auth/users (Admin only)
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = authService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // GET /auth/validate
    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateToken(
            @RequestHeader("Authorization") String token) {
        boolean isValid = authService.validateToken(token);
        return ResponseEntity.ok(isValid);
    }
}