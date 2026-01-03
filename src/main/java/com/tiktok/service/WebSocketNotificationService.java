package com.tiktok.service;

import com.tiktok.dto.NotificationDTO;
import com.tiktok.dto.MessageDTO;
import com.tiktok.dto.ChatListItemDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Gửi notification đến user cụ thể qua WebSocket
     */
    public void sendNotificationToUser(Long userId, NotificationDTO notification) {
        try {
            String destination = "/user/" + userId + "/queue/notifications";
            messagingTemplate.convertAndSend(destination, notification);
            log.info("Notification sent to user {}: {}", userId, notification.getType());
        } catch (Exception e) {
            log.error("Error sending notification to user {}: {}", userId, e.getMessage(), e);
        }
    }

    /**
     * Gửi notification về like video
     */
    public void sendLikeNotification(Long videoOwnerId, Long likerId, String likerName, Long videoId,
            String videoThumbUrl) {
        NotificationDTO notification = NotificationDTO.builder()
                .type("VIDEO_LIKED")
                .message(likerName + " đã thích video của bạn")
                .userId(likerId)
                .userName(likerName)
                .videoId(videoId)
                .videoThumbUrl(videoThumbUrl)
                .timestamp(java.time.LocalDateTime.now())
                .build();

        sendNotificationToUser(videoOwnerId, notification);
    }

    /**
     * Gửi notification khi có comment mới trên video
     */
    public void sendCommentNotification(Long videoOwnerId, Long commenterId, String commenterName,
            Long videoId, String videoThumbUrl, Long commentId, String commentContent) {
        NotificationDTO notification = NotificationDTO.builder()
                .type("COMMENT")
                .message(commenterName + " đã bình luận video của bạn: " + commentContent)
                .userId(commenterId)
                .userName(commenterName)
                .videoId(videoId)
                .videoThumbUrl(videoThumbUrl)
                .commentId(commentId)
                .commentContent(commentContent)
                .timestamp(java.time.LocalDateTime.now())
                .build();

        sendNotificationToUser(videoOwnerId, notification);
    }

    /**
     * Gửi notification khi có reply comment
     */
    public void sendCommentReplyNotification(Long commentOwnerId, Long replierId, String replierName,
            Long videoId, String videoThumbUrl, Long commentId, String replyContent) {
        NotificationDTO notification = NotificationDTO.builder()
                .type("COMMENT_REPLY")
                .message(replierName + " đã trả lời bình luận của bạn: " + replyContent)
                .userId(replierId)
                .userName(replierName)
                .videoId(videoId)
                .videoThumbUrl(videoThumbUrl)
                .commentId(commentId)
                .commentContent(replyContent)
                .timestamp(java.time.LocalDateTime.now())
                .build();

        sendNotificationToUser(commentOwnerId, notification);
    }

    /**
     * Gửi tin nhắn đến user cụ thể qua WebSocket
     */
    public void sendMessageToUser(Long receiverId, MessageDTO message) {
        try {
            String destination = "/user/" + receiverId + "/queue/messages";
            messagingTemplate.convertAndSend(destination, message);
            log.info("Message sent from user {} to user {}", message.getSenderId(), receiverId);
        } catch (Exception e) {
            log.error("Error sending message to user {}: {}", receiverId, e.getMessage(), e);
        }
    }

    /**
     * Cập nhật chat list item cho cả sender và receiver
     */
    public void updateChatList(Long userId, ChatListItemDTO chatListItem) {
        try {
            String destination = "/user/" + userId + "/queue/chat-list";
            messagingTemplate.convertAndSend(destination, chatListItem);
            log.info("Chat list updated for user {}", userId);
        } catch (Exception e) {
            log.error("Error updating chat list for user {}: {}", userId, e.getMessage(), e);
        }
    }

    /**
     * Gửi tin nhắn và cập nhật chat list cho cả 2 bên
     */
    public void sendMessage(MessageDTO message, ChatListItemDTO senderChatItem, ChatListItemDTO receiverChatItem) {
        // Gửi tin nhắn đến receiver
        sendMessageToUser(message.getReceiverId(), message);

        // Cập nhật chat list cho sender
        updateChatList(message.getSenderId(), senderChatItem);

        // Cập nhật chat list cho receiver
        updateChatList(message.getReceiverId(), receiverChatItem);
    }

    /**
     * Thông báo typing status
     */
    public void sendTypingStatus(Long senderId, Long receiverId, boolean isTyping) {
        try {
            TypingStatusDTO typingStatus = new TypingStatusDTO(senderId, isTyping);
            String destination = "/user/" + receiverId + "/queue/typing";
            messagingTemplate.convertAndSend(destination, typingStatus);
            log.debug("Typing status sent from user {} to user {}: {}", senderId, receiverId, isTyping);
        } catch (Exception e) {
            log.error("Error sending typing status: {}", e.getMessage(), e);
        }
    }

    /**
     * Thông báo tin nhắn đã được đọc
     */
    public void sendMessageReadStatus(Long senderId, Long receiverId, Long messageId) {
        try {
            MessageReadStatusDTO readStatus = new MessageReadStatusDTO(messageId, true);
            String destination = "/user/" + senderId + "/queue/read-status";
            messagingTemplate.convertAndSend(destination, readStatus);
            log.info("Read status sent for message {} from user {} to user {}", messageId, receiverId, senderId);
        } catch (Exception e) {
            log.error("Error sending read status: {}", e.getMessage(), e);
        }
    }

    /**
     * DTO cho typing status
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class TypingStatusDTO {
        private Long userId;
        private Boolean isTyping;
    }

    /**
     * DTO cho message read status
     */
    @lombok.Data
    @lombok.AllArgsConstructor
    @lombok.NoArgsConstructor
    public static class MessageReadStatusDTO {
        private Long messageId;
        private Boolean isRead;
    }
}