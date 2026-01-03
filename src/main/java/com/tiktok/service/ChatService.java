package com.tiktok.service;

import com.tiktok.dto.*;
import com.tiktok.model.ChatConversation;
import com.tiktok.repository.ChatConversationRepository;
import com.tiktok.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final MessageService messageService;
    private final UserService userService;
    private final WebSocketNotificationService webSocketNotificationService;

    @Transactional(readOnly = true)
    public List<ChatListItemDTO> getChatList(Long userId) {
        userService.findById(userId);

        List<ChatConversation> conversations = conversationRepository.findAllByUserId(userId);

        List<ChatListItemDTO> chatList = new ArrayList<>();

        for (ChatConversation conversation : conversations) {

            Long otherUserId = conversation.getUserId1().equals(userId)
                    ? conversation.getUserId2()
                    : conversation.getUserId1();

            UserSimpleDTO otherUser = userService.getUserSimpleById(otherUserId);

            MessageDTO lastMessage = messageService.getLastMessage(userId, otherUserId);

            Integer unreadCount = messageService.getUnreadCount(userId, otherUserId);

            chatList.add(new ChatListItemDTO(
                    conversation.getId(),
                    otherUser,
                    lastMessage,
                    unreadCount,
                    conversation.getUpdatedAt()));
        }

        return chatList;
    }

    /**
     * Lấy thông tin chat list item cho một cuộc hội thoại cụ thể
     * Dùng để cập nhật real-time qua WebSocket
     */
    @Transactional(readOnly = true)
    public ChatListItemDTO getChatListItem(Long userId, Long otherUserId) {
        // Validate users exist
        userService.findById(userId);
        UserSimpleDTO otherUser = userService.getUserSimpleById(otherUserId);

        // Get last message
        MessageDTO lastMessage = messageService.getLastMessage(userId, otherUserId);

        // Get unread count
        Integer unreadCount = messageService.getUnreadCount(userId, otherUserId);

        // Get or create conversation
        ChatConversation conversation = conversationRepository
                .findByUsers(userId, otherUserId)
                .orElseGet(() -> {
                    ChatConversation newConv = new ChatConversation();
                    newConv.setUserId1(userId);
                    newConv.setUserId2(otherUserId);
                    newConv.setUpdatedAt(LocalDateTime.now());
                    return conversationRepository.save(newConv);
                });

        return new ChatListItemDTO(
                conversation.getId(),
                otherUser,
                lastMessage,
                unreadCount,
                lastMessage != null ? lastMessage.getCreatedAt() : conversation.getUpdatedAt());
    }

    @Transactional()
    public ChatDetailResponse getChatDetail(Long userId, Long otherUserId) {
        // Validate users exist
        userService.findById(userId);
        UserSimpleDTO otherUser = userService.getUserSimpleById(otherUserId);

        // Get conversation
        ChatConversation conversation = conversationRepository
                .findByUsers(userId, otherUserId)
                .orElseGet(() -> {
                    ChatConversation newConv = new ChatConversation();
                    newConv.setUserId1(userId);
                    newConv.setUserId2(otherUserId);
                    newConv.setUpdatedAt(LocalDateTime.now());
                    return conversationRepository.save(newConv);
                });

        // Get all messages
        List<MessageDTO> messages = messageService.getConversationMessages(userId, otherUserId);

        return new ChatDetailResponse(conversation.getId(), otherUser, messages);
    }

    @Transactional
    public MessageDTO sendMessage(Long senderId, SendMessageRequest request) {
        // Send message
        MessageDTO message = messageService.sendMessage(senderId, request);

        // Update or create conversation
        ChatConversation conversation = conversationRepository
                .findByUsers(senderId, request.getReceiverId())
                .orElseGet(() -> {
                    ChatConversation newConv = new ChatConversation();
                    newConv.setUserId1(senderId);
                    newConv.setUserId2(request.getReceiverId());
                    return newConv;
                });

        conversation.setLastMessageId(message.getId());
        conversation.setUpdatedAt(message.getCreatedAt());
        conversationRepository.save(conversation);

        // Gửi tin nhắn qua WebSocket đến cả sender và receiver
        ChatListItemDTO senderChatItem = getChatListItem(senderId, request.getReceiverId());
        ChatListItemDTO receiverChatItem = getChatListItem(request.getReceiverId(), senderId);

        webSocketNotificationService.sendMessage(message, senderChatItem, receiverChatItem);

        return message;
    }

    @Transactional
    public void markAsRead(Long userId, Long otherUserId) {
        messageService.markMessagesAsRead(userId, otherUserId);
    }
}