package com.tiktok.controller;

import com.tiktok.dto.ApiResponse;
import com.tiktok.dto.MessageDTO;
import com.tiktok.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MessageController {
    
    private final MessageService messageService;
    
    /**
     * Lấy tất cả tin nhắn giữa 2 user
     * GET /api/messages?userId1=1&userId2=2
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<MessageDTO>>> getMessages(
            @RequestParam Long userId1,
            @RequestParam Long userId2) {
        List<MessageDTO> messages = messageService.getConversationMessages(userId1, userId2);
        return ResponseEntity.ok(ApiResponse.success(messages));
    }
    
    /**
     * Lấy số lượng tin nhắn chưa đọc
     * GET /api/messages/unread-count?userId=1&otherUserId=2
     */
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Integer>> getUnreadCount(
            @RequestParam Long userId,
            @RequestParam Long otherUserId) {
        Integer count = messageService.getUnreadCount(userId, otherUserId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
