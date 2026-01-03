package com.tiktok.controller;

import com.tiktok.dto.*;
import com.tiktok.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  @Autowired
  private AuthService authService;

  @PostMapping("/register")
  public ResponseEntity<ApiResponse<AuthResponse>> register(
      @Valid @RequestBody RegisterRequest request) {
    AuthResponse authResponse = authService.register(request);
    return ResponseEntity.ok(ApiResponse.success("User registered successfully", authResponse));
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<AuthResponse>> login(
      @Valid @RequestBody LoginRequest request) {
    AuthResponse authResponse = authService.login(request);
    return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
      @Valid @RequestBody RefreshTokenRequest request) {
    AuthResponse authResponse = authService.refreshToken(request);
    return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", authResponse));
  }

  @PostMapping("/logout")
  public ResponseEntity<ApiResponse<String>> logout(
      @RequestBody(required = false) RefreshTokenRequest request) {
    if (request != null && request.getRefreshToken() != null) {
      authService.logout(request.getRefreshToken());
    }
    return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
  }
}