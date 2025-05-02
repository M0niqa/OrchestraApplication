package com.monika.worek.orchestra.repository;

import com.monika.worek.orchestra.model.ChatMessage;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends CrudRepository<ChatMessage, Long> {
    @Query("SELECT c FROM ChatMessage c WHERE (c.senderId = :senderId AND c.receiverId = :receiverId) OR (c.senderId = :receiverId AND c.receiverId = :senderId)")
    List<ChatMessage> findChatMessagesBySenderAndReceiver(
            @Param("senderId") Long senderId,
            @Param("receiverId") Long receiverId
    );

    @Query("""
    SELECT DISTINCT CASE
        WHEN c.senderId = :userId THEN c.receiverId
        ELSE c.senderId
    END
    FROM ChatMessage c
    WHERE c.senderId = :userId OR c.receiverId = :userId
    """)
    List<Long> findChatPartnerIds(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE ChatMessage c SET c.read = true WHERE c.senderId = :senderId AND c.receiverId = :receiverId AND c.read = false")
    void markMessagesAsRead(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);

    @Query("SELECT DISTINCT c.senderId FROM ChatMessage c WHERE c.receiverId = :receiverId AND c.read = false")
    List<Long> findUnreadSenderIds(@Param("receiverId") Long receiverId);

    @Query("SELECT COUNT(c) > 0 FROM ChatMessage c WHERE c.receiverId = :receiverId AND c.read = false")
    boolean existsUnreadByReceiverId(@Param("receiverId") Long receiverId);
}