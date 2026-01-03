package com.tiktok.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatListItemDTO {
    private Long id;
    private UserSimpleDTO user;
    private MessageDTO lastMessage;
    private Integer unreadCount;
    private LocalDateTime updatedAt;
}
