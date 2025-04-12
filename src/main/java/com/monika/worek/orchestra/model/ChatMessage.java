package com.monika.worek.orchestra.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;
    private Long receiverId;
    private String messageContent;
    private LocalDateTime timestamp;

    public ChatMessage() {}

    public ChatMessage(Long senderId, Long receiverId, String messageContent) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageContent = messageContent;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "timestamp=" + timestamp +
                ", messageContent='" + messageContent + '\'' +
                ", receiverId=" + receiverId +
                ", senderId=" + senderId +
                ", id=" + id +
                '}';
    }
}