package com.tiktok.controller;

import com.tiktok.dto.ApiResponse;
import com.tiktok.model.User;
import com.tiktok.model.Video;
import com.tiktok.service.UserService;
import com.tiktok.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "User", description = "User management APIs")
public class UserController {
    
    private final UserService userService;
    private final VideoService videoService;
    
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve a list of all users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success(users));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their ID")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }
    
    @GetMapping("/@{nickname}")
    @Operation(summary = "Get user by nickname", description = "Retrieve a specific user by their nickname")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserByNickname(@PathVariable String nickname) {
        User user = userService.getUserByNickname(nickname);
        List<Video> videos = videoService.getVideosByUserId(user.getId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("videos", videos);
        
        return ResponseEntity.ok(ApiResponse.success(response));
    }
    
    @PostMapping
    @Operation(summary = "Create user", description = "Register a new user")
    public ResponseEntity<ApiResponse<User>> createUser(@Valid @RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", createdUser));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update user", description = "Update an existing user's profile")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody User userDetails) {
        User updatedUser = userService.updateUser(id, userDetails);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", updatedUser));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a user by ID")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }
    
    @PostMapping("/{userId}/follow/{targetUserId}")
    @Operation(summary = "Follow user", description = "Follow another user")
    public ResponseEntity<ApiResponse<User>> followUser(
            @PathVariable Long userId,
            @PathVariable Long targetUserId) {
        User user = userService.followUser(userId, targetUserId);
        return ResponseEntity.ok(ApiResponse.success("User followed successfully", user));
    }
    
    @DeleteMapping("/{userId}/follow/{targetUserId}")
    @Operation(summary = "Unfollow user", description = "Unfollow a user")
    public ResponseEntity<ApiResponse<User>> unfollowUser(
            @PathVariable Long userId,
            @PathVariable Long targetUserId) {
        User user = userService.unfollowUser(userId, targetUserId);
        return ResponseEntity.ok(ApiResponse.success("User unfollowed successfully", user));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Search users by nickname or name")
    public ResponseEntity<ApiResponse<List<User>>> searchUsers(@RequestParam String query) {
        List<User> users = userService.searchUsers(query);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<User>> getUserByEmail(@PathVariable String email) {
        User user = userService.getUserById(
            userService.findByEmail(email).getId()
        );
        return ResponseEntity.ok(ApiResponse.success(user));
    }
}
