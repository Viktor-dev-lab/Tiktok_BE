package com.tiktok.controller;

import com.tiktok.dto.*;
import com.tiktok.service.ChatService;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.tiktok.service.WebSocketNotificationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {
    
    private final ChatService chatService;
    private final WebSocketNotificationService webSocketNotificationService;
    
    /**
     * Lấy danh sách cuộc hội thoại của user
     * GET /api/chats?userId=1
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ChatListItemDTO>>> getChatList(
            @RequestParam Long userId) {
        List<ChatListItemDTO> chatList = chatService.getChatList(userId);
        return ResponseEntity.ok(ApiResponse.success(chatList));
    }
    
    /**
     * Lấy chi tiết cuộc hội thoại giữa 2 user
     * GET /api/chats/detail?userId=1&otherUserId=2
     */
    @GetMapping("/detail")
    public ResponseEntity<ApiResponse<ChatDetailResponse>> getChatDetail(
            @RequestParam Long userId,
            @RequestParam Long otherUserId) {
        ChatDetailResponse chatDetail = chatService.getChatDetail(userId, otherUserId);
        return ResponseEntity.ok(ApiResponse.success(chatDetail));
    }
    
    /**
     * Gửi tin nhắn qua REST API (không dùng WebSocket)
     * POST /api/chats/messages?senderId=1
     * Body: { "receiverId": 2, "content": "Hello!" }
     * 
     * Phương thức này sẽ:
     * 1. Lưu tin nhắn vào database
     * 2. Gửi tin nhắn real-time qua WebSocket đến receiver
     * 3. Cập nhật chat list cho cả sender và receiver qua WebSocket
     */
    @PostMapping("/messages")
    public ResponseEntity<ApiResponse<MessageDTO>> sendMessage(
            @RequestParam Long senderId,
            @Valid @RequestBody SendMessageRequest request) {
        
        // Lưu tin nhắn và lấy thông tin đầy đủ (bao gồm cả ChatListItemDTO)
        MessageDTO message = chatService.sendMessage(senderId, request);
        
        // Lấy ChatListItemDTO cho sender và receiver
        // (giả sử chatService.sendMessage trả về đầy đủ thông tin)
        // Hoặc có thể lấy từ chatService.getChatListItem(senderId, receiverId)
        ChatListItemDTO senderChatItem = chatService.getChatListItem(senderId, request.getReceiverId());
        ChatListItemDTO receiverChatItem = chatService.getChatListItem(request.getReceiverId(), senderId);
        
        // Gửi tin nhắn qua WebSocket (sử dụng method có sẵn trong WebSocketNotificationService)
        webSocketNotificationService.sendMessage(message, senderChatItem, receiverChatItem);
        
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success("Message sent successfully", message));
    }
    
    /**
     * Đánh dấu tin nhắn đã đọc
     * PUT /api/chats/mark-read?userId=1&otherUserId=2
     */
    @PutMapping("/mark-read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @RequestParam Long userId,
            @RequestParam Long otherUserId) {
        chatService.markAsRead(userId, otherUserId);
        return ResponseEntity.ok(ApiResponse.success("Messages marked as read", null));
    }
}
