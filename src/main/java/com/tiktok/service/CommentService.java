package com.tiktok.service;

import com.tiktok.exception.ResourceNotFoundException;
import com.tiktok.model.Comment;
import com.tiktok.model.User;
import com.tiktok.model.Video;
import com.tiktok.repository.CommentRepository;
import com.tiktok.repository.UserRepository;
import com.tiktok.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    
    private final CommentRepository commentRepository;
    private final VideoRepository videoRepository;
    private final UserRepository userRepository;
    
    /**
     * Tạo comment mới cho video
     */
    public Comment createComment(Long videoId, Long userId, String content) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + videoId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Comment comment = new Comment();
        comment.setVideo(video);
        comment.setUser(user);
        comment.setContent(content);
        comment.setParentComment(null); // Comment gốc
        
        Comment savedComment = commentRepository.save(comment);
        
        // Tăng comments_count của video
        video.setCommentsCount(video.getCommentsCount() + 1);
        videoRepository.save(video);
        
        return savedComment;
    }
    
    /**
     * Tạo reply cho một comment
     */
    public Comment createReply(Long parentCommentId, Long userId, String content) {
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + parentCommentId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Video video = parentComment.getVideo();
        
        Comment reply = new Comment();
        reply.setVideo(video);
        reply.setUser(user);
        reply.setContent(content);
        reply.setParentComment(parentComment);
        
        Comment savedReply = commentRepository.save(reply);
        
        // Tăng comments_count của video
        video.setCommentsCount(video.getCommentsCount() + 1);
        videoRepository.save(video);
        
        return savedReply;
    }
    
    /**
     * Lấy tất cả comments của video (chỉ comment gốc, không bao gồm replies)
     */
    public List<Comment> getCommentsByVideoId(Long videoId) {
        List<Comment> comments = commentRepository.findByVideoIdAndParentCommentIsNullOrderByCreatedAtDesc(videoId);
        
        // Load replies cho mỗi comment
        return comments.stream()
                .map(comment -> {
                    List<Comment> replies = commentRepository.findByParentCommentIdOrderByCreatedAtAsc(comment.getId());
                    comment.setReplies(replies);
                    return comment;
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Like/Unlike comment
     */
    public Comment likeComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        
        // Toggle like (logic đơn giản, có thể cải thiện bằng cách lưu vào bảng like_comment)
        // Ở đây tạm thời chỉ tăng/giảm likesCount
        comment.setLikesCount(comment.getLikesCount() + 1);
        
        return commentRepository.save(comment);
    }
    
    /**
     * Unlike comment
     */
    public Comment unlikeComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        
        comment.setLikesCount(Math.max(0, comment.getLikesCount() - 1));
        
        return commentRepository.save(comment);
    }
    
    /**
     * Xóa comment
     */
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        
        Video video = comment.getVideo();
        
        // Đếm số replies sẽ bị xóa
        Long repliesCount = commentRepository.countByParentCommentId(commentId);
        
        // Xóa comment và tất cả replies
        commentRepository.delete(comment);
        
        // Giảm comments_count của video
        int totalDeleted = 1 + repliesCount.intValue(); // 1 comment gốc + số replies
        video.setCommentsCount(Math.max(0, video.getCommentsCount() - totalDeleted));
        videoRepository.save(video);
    }
    
    /**
     * Lấy comment theo ID
     */
    public Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
    }
}