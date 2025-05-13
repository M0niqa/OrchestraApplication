package com.monika.worek.orchestra.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class ChatMessageDTO {
    private Long senderId;
    private Long receiverId;
    private String messageContent;
    private LocalDateTime timestamp;
    private boolean read;
}
