package com.tiktok.repository;

import com.tiktok.model.ChatConversation;
import com.tiktok.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE " +
            "(m.senderId = :userId1 AND m.receiverId = :userId2) OR " +
            "(m.senderId = :userId2 AND m.receiverId = :userId1) " +
            "ORDER BY m.createdAt ASC")
    List<Message> findConversationMessages(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2);

    @Query("SELECT m FROM Message m WHERE " +
            "(m.senderId = :userId1 AND m.receiverId = :userId2) OR " +
            "(m.senderId = :userId2 AND m.receiverId = :userId1) " +
            "ORDER BY m.createdAt DESC")
    List<Message> findLastMessageBetweenUsers(
            @Param("userId1") Long userId1,
            @Param("userId2") Long userId2);

    @Query("SELECT COUNT(m) FROM Message m WHERE " +
            "m.receiverId = :userId AND m.senderId = :otherUserId AND m.isRead = false")
    Integer countUnreadMessages(
            @Param("userId") Long userId,
            @Param("otherUserId") Long otherUserId);

    @Modifying
    @Query("UPDATE Message m SET m.isRead = true WHERE " +
            "m.receiverId = :receiverId AND m.senderId = :senderId AND m.isRead = false")
    void markMessagesAsRead(
            @Param("receiverId") Long receiverId,
            @Param("senderId") Long senderId);

    @Query("SELECT c FROM ChatConversation c " +
            "WHERE c.userId1 = :userId OR c.userId2 = :userId " +
            "ORDER BY c.updatedAt DESC")
    List<ChatConversation> findAllByUserId(Long userId);

}
