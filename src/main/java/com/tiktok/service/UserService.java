package com.tiktok.service;

import com.tiktok.exception.ResourceNotFoundException;
import com.tiktok.model.User;
import com.tiktok.repository.UserRepository;
import com.tiktok.dto.UserDTO;
import com.tiktok.dto.UserSimpleDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
    
    public User getUserByNickname(String nickname) {
        // Remove @ symbol if present
        String cleanNickname = nickname.startsWith("@") ? nickname.substring(1) : nickname;
        return userRepository.findByNickname(cleanNickname)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with nickname: " + cleanNickname));
    }
    
    public User createUser(User user) {
        if (userRepository.existsByNickname(user.getNickname())) {
            throw new IllegalArgumentException("Nickname already exists: " + user.getNickname());
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + user.getEmail());
        }
        return userRepository.save(user);
    }
    
    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        
        user.setFirstName(userDetails.getFirstName());
        user.setLastName(userDetails.getLastName());
        user.setBio(userDetails.getBio());
        user.setAvatar(userDetails.getAvatar());
        user.setWebsiteUrl(userDetails.getWebsiteUrl());
        user.setFacebookUrl(userDetails.getFacebookUrl());
        user.setYoutubeUrl(userDetails.getYoutubeUrl());
        user.setTwitterUrl(userDetails.getTwitterUrl());
        user.setInstagramUrl(userDetails.getInstagramUrl());
        
        return userRepository.save(user);
    }
    
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
    
    public User followUser(Long userId, Long targetUserId) {
        User targetUser = getUserById(targetUserId);
        targetUser.setFollowersCount(targetUser.getFollowersCount() + 1);
        targetUser.setIsFollowed(true);
        return userRepository.save(targetUser);
    }
    
    public User unfollowUser(Long userId, Long targetUserId) {
        User targetUser = getUserById(targetUserId);
        targetUser.setFollowersCount(Math.max(0, targetUser.getFollowersCount() - 1));
        targetUser.setIsFollowed(false);
        return userRepository.save(targetUser);
    }
    
    public List<User> searchUsers(String query) {
        return userRepository.searchUsers(query);
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
    
    @Transactional(readOnly = true)
    public User findByNickname(String nickname) {
        return userRepository.findByNickname(nickname)
            .orElseThrow(() -> new ResourceNotFoundException("User", "nickname", nickname));
    }
    
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }
    
    
    @Transactional(readOnly = true)
    public UserSimpleDTO getUserSimpleById(Long id) {
        User user = findById(id);
        return convertToSimpleDTO(user);
    }
    
    
    private UserSimpleDTO convertToSimpleDTO(User user) {
        return new UserSimpleDTO(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getNickname(),
            user.getAvatar(),
            user.getTick()
        );
    }
}
