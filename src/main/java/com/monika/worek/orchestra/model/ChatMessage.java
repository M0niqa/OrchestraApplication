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
    private boolean read = false;

    public ChatMessage() {}

    public ChatMessage(Long senderId, Long receiverId, String messageContent, LocalDateTime timestamp, boolean read) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageContent = messageContent;
        this.timestamp = timestamp;
        this.read = read;
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