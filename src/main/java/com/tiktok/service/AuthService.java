package com.tiktok.service;

import com.tiktok.dto.*;
import com.tiktok.exception.BadRequestException;
import com.tiktok.exception.TokenRefreshException;
import com.tiktok.exception.UnauthorizedException;
import com.tiktok.model.RefreshToken;
import com.tiktok.model.User;
import com.tiktok.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JwtService jwtService;

  @Autowired
  private RefreshTokenService refreshTokenService;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private CustomUserDetailsService userDetailsService;

  @Value("${jwt.expiration}")
  private Long jwtExpiration;

  @Transactional
  public AuthResponse register(RegisterRequest request) {
    // Check if email exists
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new BadRequestException("Email is already taken!");
    }

    // Check if nickname exists
    if (userRepository.existsByNickname(request.getNickname())) {
      throw new BadRequestException("Nickname is already taken!");
    }

    // Create new user
    User user = new User();
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setNickname(request.getNickname());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setTick(false);
    user.setFollowingsCount(0);
    user.setFollowersCount(0);
    user.setLikesCount(0);

    user = userRepository.save(user);

    // Generate tokens
    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
    String accessToken = jwtService.generateToken(userDetails);
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

    return AuthResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken.getToken())
        .expiresIn(jwtExpiration / 1000)
        .user(convertToUserDTO(user))
        .build();
  }

  public AuthResponse login(LoginRequest request) {
    // Authenticate user
    Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
            request.getEmail(),
            request.getPassword()));

    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

    // Generate tokens
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    String accessToken = jwtService.generateToken(userDetails);
    RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

    return AuthResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken.getToken())
        .expiresIn(jwtExpiration / 1000)
        .user(convertToUserDTO(user))
        .build();
  }

  public AuthResponse refreshToken(RefreshTokenRequest request) {
    String requestRefreshToken = request.getRefreshToken();

    return refreshTokenService.findByToken(requestRefreshToken)
        .map(refreshTokenService::verifyExpiration)
        .map(RefreshToken::getUser)
        .map(user -> {
          UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
          String accessToken = jwtService.generateToken(userDetails);

          return AuthResponse.builder()
              .accessToken(accessToken)
              .refreshToken(requestRefreshToken)
              .expiresIn(jwtExpiration / 1000)
              .user(convertToUserDTO(user))
              .build();
        })
        .orElseThrow(() -> new TokenRefreshException("Refresh token is not in database!"));
  }

  @Transactional
  public void logout(String refreshToken) {
    if (refreshToken != null && !refreshToken.isEmpty()) {
      refreshTokenService.deleteByToken(refreshToken);
    }
  }

  private UserDTO convertToUserDTO(User user) {
    return UserDTO.builder()
        .id(user.getId())
        .firstName(user.getFirstName())
        .lastName(user.getLastName())
        .nickname(user.getNickname())
        .email(user.getEmail())
        .avatar(user.getAvatar())
        .bio(user.getBio())
        .tick(user.getTick())
        .followingsCount(user.getFollowingsCount())
        .followersCount(user.getFollowersCount())
        .likesCount(user.getLikesCount())
        .websiteUrl(user.getWebsiteUrl())
        .facebookUrl(user.getFacebookUrl())
        .youtubeUrl(user.getYoutubeUrl())
        .twitterUrl(user.getTwitterUrl())
        .instagramUrl(user.getInstagramUrl())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .build();
  }
}