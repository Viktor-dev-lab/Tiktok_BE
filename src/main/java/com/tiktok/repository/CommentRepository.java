package com.tiktok.repository;

import com.tiktok.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // Lấy tất cả comment gốc (không phải reply) của video, sắp xếp theo thời gian mới nhất
    List<Comment> findByVideoIdAndParentCommentIsNullOrderByCreatedAtDesc(Long videoId);
    
    // Lấy tất cả replies của một comment
    List<Comment> findByParentCommentIdOrderByCreatedAtAsc(Long parentCommentId);
    
    // Đếm số comment của video (bao gồm cả replies)
    Long countByVideoId(Long videoId);
    
    // Đếm số replies của một comment
    Long countByParentCommentId(Long parentCommentId);
}