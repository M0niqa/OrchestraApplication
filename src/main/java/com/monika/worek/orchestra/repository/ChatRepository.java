package com.monika.worek.orchestra.repository;

import com.monika.worek.orchestra.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
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
}