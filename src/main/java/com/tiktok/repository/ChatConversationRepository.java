package com.tiktok.repository;

import com.tiktok.model.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {
    
    @Query("SELECT c FROM ChatConversation c WHERE " +
           "(c.userId1 = :userId1 AND c.userId2 = :userId2) OR " +
           "(c.userId1 = :userId2 AND c.userId2 = :userId1)")
    Optional<ChatConversation> findByUsers(
        @Param("userId1") Long userId1, 
        @Param("userId2") Long userId2
    );
    
    @Query("SELECT c FROM ChatConversation c WHERE " +
           "c.userId1 = :userId OR c.userId2 = :userId " +
           "ORDER BY c.updatedAt DESC")
    List<ChatConversation> findAllByUserId(@Param("userId") Long userId);
}
