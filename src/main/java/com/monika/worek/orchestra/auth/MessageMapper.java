package com.monika.worek.orchestra.auth;

import com.monika.worek.orchestra.dto.ChatMessageDTO;
import com.monika.worek.orchestra.model.ChatMessage;

public class MessageMapper {

    public static ChatMessage mapToEntity(ChatMessageDTO dto) {
        if (dto == null) {
            return null;
        }
        return new ChatMessage(
                dto.getSenderId(),
                dto.getReceiverId(),
                dto.getMessageContent(),
                dto.getTimestamp()
        );
    }

    public static ChatMessageDTO mapToDto(ChatMessage message) {
        if (message == null) {
            return null;
        }
        return new ChatMessageDTO(
                message.getSenderId(),
                message.getReceiverId(),
                message.getMessageContent(),
                message.getTimestamp(),
                message.isRead()
        );
    }
}
