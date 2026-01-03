package com.tiktok.controller;

import com.tiktok.dto.ApiResponse;
import com.tiktok.model.Comment;
import com.tiktok.model.User;
import com.tiktok.model.Video;
import com.tiktok.service.CommentService;
import com.tiktok.service.UserService;
import com.tiktok.service.VideoService;
import com.tiktok.service.WebSocketNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/videos/{videoId}/comments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Comment", description = "Comment management APIs")
@Slf4j
public class CommentController {

    private final CommentService commentService;
    private final VideoService videoService;
    private final UserService userService;
    private final WebSocketNotificationService webSocketNotificationService;

    /**
     * Tạo comment mới cho video
     */
    @PostMapping
    @Operation(summary = "Create comment", description = "Create a new comment on a video")
    public ResponseEntity<ApiResponse<Comment>> createComment(
            @PathVariable Long videoId,
            @RequestParam("user_id") Long userId,
            @RequestParam("content") @NotBlank String content) {
        
        // Lấy thông tin user
        User commenter = userService.getUserById(userId);
        
        // Tạo comment
        Comment comment = commentService.createComment(videoId, userId, content);
        
        // Lấy thông tin video và owner
        Video video = videoService.getVideoById(videoId);
        User videoOwner = video.getUser();
        
        // Gửi notification real-time nếu không phải chính chủ video comment
        if (!videoOwner.getId().equals(userId)) {
            webSocketNotificationService.sendCommentNotification(
                    videoOwner.getId(),
                    userId,
                    commenter.getFullName(),
                    videoId,
                    video.getThumbUrl(),
                    comment.getId(),
                    content
            );
        }
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Comment created successfully", comment));
    }

    /**
     * Lấy danh sách comments của video
     */
    @GetMapping
    @Operation(summary = "Get comments", description = "Get all comments of a video")
    public ResponseEntity<ApiResponse<List<Comment>>> getComments(@PathVariable Long videoId) {
        List<Comment> comments = commentService.getCommentsByVideoId(videoId);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    /**
     * Tạo reply cho một comment
     */
    @PostMapping("/{commentId}/replies")
    @Operation(summary = "Reply to comment", description = "Create a reply to a comment")
    public ResponseEntity<ApiResponse<Comment>> createReply(
            @PathVariable Long videoId,
            @PathVariable Long commentId,
            @RequestParam("user_id") Long userId,
            @RequestParam("content") @NotBlank String content) {
        
        // Lấy thông tin user
        User replier = userService.getUserById(userId);
        
        // Tạo reply
        Comment reply = commentService.createReply(commentId, userId, content);
        
        // Lấy thông tin comment gốc và owner
        Comment parentComment = commentService.getCommentById(commentId);
        User commentOwner = parentComment.getUser();
        
        Video video = videoService.getVideoById(videoId);
        
        // Gửi notification real-time nếu không phải chính chủ comment reply
        if (!commentOwner.getId().equals(userId)) {
            webSocketNotificationService.sendCommentReplyNotification(
                    commentOwner.getId(),
                    userId,
                    replier.getFullName(),
                    videoId,
                    video.getThumbUrl(),
                    commentId,
                    content
            );
        }
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Reply created successfully", reply));
    }

    /**
     * Like comment
     */
    @PostMapping("/{commentId}/like")
    @Operation(summary = "Like comment", description = "Like a comment")
    public ResponseEntity<ApiResponse<Comment>> likeComment(
            @PathVariable Long videoId,
            @PathVariable Long commentId,
            @RequestParam("user_id") Long userId) {
        
        Comment comment = commentService.likeComment(commentId, userId);
        return ResponseEntity.ok(ApiResponse.success(comment));
    }

    /**
     * Unlike comment
     */
    @DeleteMapping("/{commentId}/like")
    @Operation(summary = "Unlike comment", description = "Unlike a comment")
    public ResponseEntity<ApiResponse<Comment>> unlikeComment(
            @PathVariable Long videoId,
            @PathVariable Long commentId) {
        
        Comment comment = commentService.unlikeComment(commentId);
        return ResponseEntity.ok(ApiResponse.success(comment));
    }

    /**
     * Xóa comment
     */
    @DeleteMapping("/{commentId}")
    @Operation(summary = "Delete comment", description = "Delete a comment")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long videoId,
            @PathVariable Long commentId) {
        
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully", null));
    }
}