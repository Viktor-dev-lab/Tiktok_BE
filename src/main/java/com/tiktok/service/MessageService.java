package com.tiktok.service;

import com.tiktok.dto.MessageDTO;
import com.tiktok.dto.SendMessageRequest;
import com.tiktok.exception.ResourceNotFoundException;
import com.tiktok.model.Message;
import com.tiktok.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    
    private final MessageRepository messageRepository;
    private final UserService userService;
    
    @Transactional
    public MessageDTO sendMessage(Long senderId, SendMessageRequest request) {
        // Validate users exist
        userService.findById(senderId);
        userService.findById(request.getReceiverId());
        
        Message message = new Message();
        message.setSenderId(senderId);
        message.setReceiverId(request.getReceiverId());
        message.setContent(request.getContent());
        message.setCreatedAt(LocalDateTime.now());
        message.setIsRead(false);
        
        Message savedMessage = messageRepository.save(message);
        return convertToDTO(savedMessage);
    }
    
    @Transactional(readOnly = true)
    public List<MessageDTO> getConversationMessages(Long userId1, Long userId2) {
        // Validate users exist
        userService.findById(userId1);
        userService.findById(userId2);
        
        List<Message> messages = messageRepository.findConversationMessages(userId1, userId2);
        return messages.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public void markMessagesAsRead(Long receiverId, Long senderId) {
        messageRepository.markMessagesAsRead(receiverId, senderId);
    }
    
    @Transactional(readOnly = true)
    public MessageDTO getLastMessage(Long userId1, Long userId2) {
        List<Message> messages = messageRepository.findLastMessageBetweenUsers(userId1, userId2);
        if (messages.isEmpty()) {
            return null;
        }
        return convertToDTO(messages.get(0));
    }
    
    @Transactional(readOnly = true)
    public Integer getUnreadCount(Long userId, Long otherUserId) {
        return messageRepository.countUnreadMessages(userId, otherUserId);
    }
    
    @Transactional(readOnly = true)
    public Message findById(Long id) {
        return messageRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Message", "id", id));
    }
    
    private MessageDTO convertToDTO(Message message) {
        return new MessageDTO(
            message.getId(),
            message.getSenderId(),
            message.getReceiverId(),
            message.getContent(),
            message.getCreatedAt(),
            message.getIsRead()
        );
    }
}