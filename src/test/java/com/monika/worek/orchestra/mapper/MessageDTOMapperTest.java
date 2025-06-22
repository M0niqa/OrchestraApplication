package com.monika.worek.orchestra.mapper;


import com.monika.worek.orchestra.dto.ChatMessageDTO;
import com.monika.worek.orchestra.mappers.MessageDTOMapper;
import com.monika.worek.orchestra.model.ChatMessage;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class MessageDTOMapperTest {

    @Test
    void mapToEntity_whenGivenNullDto_thenShouldReturnNull() {
        // given
        ChatMessageDTO dto = null;

        // when
        ChatMessage entity = MessageDTOMapper.mapToEntity(dto);

        // then
        assertThat(entity).isNull();
    }

    @Test
    void mapToEntity_whenDtoFieldIsNull_thenEntityFieldShouldAlsoBeNull() {
        // given
        ChatMessageDTO dtoWithNullField = new ChatMessageDTO(1L, 2L, null, LocalDateTime.now(), false);

        // when
        ChatMessage entity = MessageDTOMapper.mapToEntity(dtoWithNullField);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getMessageContent()).isNull();
    }

    @Test
    void mapToEntity_whenGivenValidDto_thenShouldMapToEntity() {
        // given
        LocalDateTime timestamp = LocalDateTime.of(2025, 6, 19, 21, 11);
        ChatMessageDTO dto = new ChatMessageDTO(1L, 2L, "Hello", timestamp, false);

        // when
        ChatMessage entity = MessageDTOMapper.mapToEntity(dto);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getSenderId()).isEqualTo(dto.getSenderId());
        assertThat(entity.getReceiverId()).isEqualTo(dto.getReceiverId());
        assertThat(entity.getMessageContent()).isEqualTo(dto.getMessageContent());
        assertThat(entity.getTimestamp()).isEqualTo(dto.getTimestamp());
        assertThat(entity.isRead()).isEqualTo(dto.isRead());
    }

    @Test
    void mapToDto_whenGivenNullEntity_thenShouldReturnNull() {
        // given
        ChatMessage entity = null;

        // when
        ChatMessageDTO dto = MessageDTOMapper.mapToDto(entity);

        // then
        assertThat(dto).isNull();
    }

    @Test
    void mapToDto_whenEntityFieldIsNull_thenDtoFieldShouldAlsoBeNull() {
        // given
        ChatMessage entityWithNullField = new ChatMessage(1L, 2L, null, LocalDateTime.now(), false);

        // when
        ChatMessageDTO dto = MessageDTOMapper.mapToDto(entityWithNullField);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getMessageContent()).isNull();
    }

    @Test
    void mapToDto_whenGivenValidEntity_thenShouldMapToDTO() {
        // given
        LocalDateTime timestamp = LocalDateTime.of(2025, 6, 19, 21, 11);
        ChatMessage entity = new ChatMessage(1L, 2L, "Hello", timestamp, false);

        // when
        ChatMessageDTO dto = MessageDTOMapper.mapToDto(entity);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getSenderId()).isEqualTo(entity.getSenderId());
        assertThat(dto.getReceiverId()).isEqualTo(entity.getReceiverId());
        assertThat(dto.getMessageContent()).isEqualTo(entity.getMessageContent());
        assertThat(dto.getTimestamp()).isEqualTo(entity.getTimestamp());
        assertThat(dto.isRead()).isEqualTo(entity.isRead());
    }

}