package com.tiktok.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private String type; // VIDEO_LIKED, COMMENT, COMMENT_REPLY, FOLLOW, etc.
    private String message;
    private Long userId; // ID của user thực hiện action
    private String userName; // Tên của user thực hiện action
    private Long videoId; // ID của video
    private String videoThumbUrl; // Thumbnail của video
    private Long commentId; // ID của comment (nếu là notification về comment)
    private String commentContent; // Nội dung comment (nếu là notification về comment)
    private LocalDateTime timestamp;
}