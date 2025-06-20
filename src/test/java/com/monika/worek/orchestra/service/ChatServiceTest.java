package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.dto.ChatMessageDTO;
import com.monika.worek.orchestra.dto.UserBasicDTO;
import com.monika.worek.orchestra.model.ChatMessage;
import com.monika.worek.orchestra.repository.ChatRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ChatService chatService;

    @Test
    void sendMessage_whenGivenMessageDTO_shouldSaveMessageEntity() {
        // given
        ChatMessageDTO messageDTO = new ChatMessageDTO(1L, 2L, "Hello", LocalDateTime.now(), false);

        // when
        chatService.sendMessage(messageDTO);

        // then
        verify(chatRepository, times(1)).save(any(ChatMessage.class));
    }

    @Test
    void getChatHistory_whenMessagesExist_thenShouldReturnListOfMessageDTOs() {
        // given
        Long senderId = 1L;
        Long receiverId = 2L;
        ChatMessage message1 = new ChatMessage(senderId, receiverId, "Hi", LocalDateTime.now(), true);
        ChatMessage message2 = new ChatMessage(receiverId, senderId, "Hi back", LocalDateTime.now(), true);
        List<ChatMessage> messages = List.of(message1, message2);

        when(chatRepository.findChatMessagesBySenderAndReceiver(senderId, receiverId)).thenReturn(messages);

        // when
        List<ChatMessageDTO> chatHistory = chatService.getChatHistory(senderId, receiverId);

        // then
        assertThat(chatHistory).isNotNull();
        assertThat(chatHistory).hasSize(2);
        assertThat(chatHistory.get(0).getMessageContent()).isEqualTo("Hi");
        assertThat(chatHistory.get(1).getMessageContent()).isEqualTo("Hi back");
    }

    @Test
    void getChatHistory_whenNoMessagesExist_shouldReturnEmptyList() {
        // given
        Long senderId = 1L;
        Long receiverId = 2L;
        when(chatRepository.findChatMessagesBySenderAndReceiver(senderId, receiverId)).thenReturn(Collections.emptyList());

        // when
        List<ChatMessageDTO> chatHistory = chatService.getChatHistory(senderId, receiverId);

        // then
        assertThat(chatHistory).isNotNull();
        assertThat(chatHistory).isEmpty();
    }

    @Test
    void getChatPartners_whenUserHasPartners_shouldReturnListOfUserDTOs() {
        // given
        Long userId = 1L;
        List<Long> partnerIds = List.of(2L, 3L);
        UserBasicDTO partner2 = new UserBasicDTO(2L, "John", "Doe", null);
        UserBasicDTO partner3 = new UserBasicDTO(3L, "Jane", "Smith", null);

        when(chatRepository.findChatPartnerIds(userId)).thenReturn(partnerIds);
        when(userService.findUserById(2L)).thenReturn(partner2);
        when(userService.findUserById(3L)).thenReturn(partner3);

        // when
        List<UserBasicDTO> chatPartners = chatService.getChatPartners(userId);

        // then
        assertThat(chatPartners).isNotNull();
        assertThat(chatPartners).hasSize(2);
        assertThat(chatPartners).containsExactlyInAnyOrder(partner2, partner3);
    }

    @Test
    void markMessagesAsRead_whenCalled_shouldInvokeRepositoryMethod() {
        // given
        Long senderId = 2L;
        Long receiverId = 1L;

        // when
        chatService.markMessagesAsRead(senderId, receiverId);

        // then
        verify(chatRepository, times(1)).markMessagesAsRead(senderId, receiverId);
    }

    @Test
    void getUnreadSenderIds_whenUnreadMessagesExist_shouldReturnSetOfSenderIds() {
        // given
        Long receiverId = 1L;
        List<Long> unreadSenders = List.of(5L, 8L);
        when(chatRepository.findUnreadSenderIds(receiverId)).thenReturn(unreadSenders);

        // when
        Set<Long> senderIds = chatService.getUnreadSenderIds(receiverId);

        // then
        assertThat(senderIds).isNotNull();
        assertThat(senderIds).hasSize(2);
        assertThat(senderIds).contains(5L, 8L);
    }

    @Test
    void areUnreadMessages_whenUnreadMessagesExist_shouldReturnTrue() {
        // given
        Long receiverId = 1L;
        when(chatRepository.existsUnreadByReceiverId(receiverId)).thenReturn(true);

        // when
        boolean hasUnread = chatService.areUnreadMessages(receiverId);

        // then
        assertThat(hasUnread).isTrue();
    }

    @Test
    void areUnreadMessages_whenNoUnreadMessages_shouldReturnFalse() {
        // given
        Long receiverId = 1L;
        when(chatRepository.existsUnreadByReceiverId(receiverId)).thenReturn(false);

        // when
        boolean hasUnread = chatService.areUnreadMessages(receiverId);

        // then
        assertThat(hasUnread).isFalse();
    }
}
