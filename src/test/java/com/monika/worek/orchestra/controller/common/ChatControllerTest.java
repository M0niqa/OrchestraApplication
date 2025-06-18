package com.monika.worek.orchestra.controller.common;

import com.monika.worek.orchestra.dto.ChatMessageDTO;
import com.monika.worek.orchestra.dto.UserBasicDTO;
import com.monika.worek.orchestra.service.ChatService;
import com.monika.worek.orchestra.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ChatService chatService;

    @MockBean
    private UserService userService;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ChatController chatController;

    @BeforeEach
    void setUp() {
        // given
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "sender@example.com")
    void chatPage_ifNoReceiverId_thenShouldReturnChatViewWithEmptyMessages() throws Exception {
        // given
        Long senderId = 1L;
        UserBasicDTO senderDto = new UserBasicDTO(senderId, "Sender", "User", null);
        List<UserBasicDTO> allUsers = List.of(new UserBasicDTO(2L, "Receiver", "One", null));
        List<UserBasicDTO> chatPartners = Collections.emptyList();
        Set<Long> unreadFrom = Collections.emptySet();

        when(userService.getUserBasicDtoByEmail("sender@example.com")).thenReturn(senderDto);
        when(userService.getAllBasicDTOUsers()).thenReturn(allUsers);
        when(chatService.getChatPartners(senderId)).thenReturn(chatPartners);
        when(chatService.getUnreadSenderIds(senderId)).thenReturn(unreadFrom);

        // when
        mockMvc.perform(get("/chat"))
                // then
                .andExpect(status().isOk())
                .andExpect(view().name("common/chat"))
                .andExpect(model().attribute("senderId", senderId))
                .andExpect(model().attribute("users", allUsers))
                .andExpect(model().attribute("chatPartners", chatPartners))
                .andExpect(model().attribute("unreadFrom", unreadFrom))
                .andExpect(model().attribute("receiverId", (Long) null))
                .andExpect(model().attribute("receiver", new UserBasicDTO()))
                .andExpect(model().attribute("messages", Collections.emptyList()));

        verify(userService, times(1)).getUserBasicDtoByEmail("sender@example.com");
        verify(userService, times(1)).getAllBasicDTOUsers();
        verify(chatService, times(1)).getChatPartners(senderId);
        verify(chatService, times(1)).getUnreadSenderIds(senderId);
        verify(chatService, never()).getChatHistory(any(), any());
        verify(chatService, never()).markMessagesAsRead(any(), any());
    }

    @Test
    @WithMockUser(username = "sender@example.com")
    void chatPage_ifReceiverIdChosen_thenShouldReturnChatViewWithHistory() throws Exception {
        // given
        Long senderId = 1L;
        Long receiverId = 2L;
        UserBasicDTO senderDto = new UserBasicDTO(senderId, "Sender", "User", null);
        UserBasicDTO receiverDto = new UserBasicDTO(receiverId, "Receiver", "One",null);
        List<UserBasicDTO> allUsers = List.of(receiverDto);
        List<UserBasicDTO> chatPartners = List.of(receiverDto);
        Set<Long> unreadFrom = new HashSet<>();
        unreadFrom.add(receiverId);

        List<ChatMessageDTO> chatHistory = List.of(
                new ChatMessageDTO(senderId, receiverId, "Hi!", LocalDateTime.now(), false),
                new ChatMessageDTO(receiverId, senderId, "Hello!", LocalDateTime.now(), false)
        );

        when(userService.getUserBasicDtoByEmail("sender@example.com")).thenReturn(senderDto);
        when(userService.getAllBasicDTOUsers()).thenReturn(allUsers);
        when(chatService.getChatPartners(senderId)).thenReturn(chatPartners);
        when(chatService.getUnreadSenderIds(senderId)).thenReturn(unreadFrom);
        when(userService.findUserById(receiverId)).thenReturn(receiverDto);
        when(chatService.getChatHistory(senderId, receiverId)).thenReturn(chatHistory);
        doNothing().when(chatService).markMessagesAsRead(receiverId, senderId);

        // when
        mockMvc.perform(get("/chat/{receiverId}", receiverId))
                // then
                .andExpect(status().isOk())
                .andExpect(view().name("common/chat"))
                .andExpect(model().attribute("senderId", senderId))
                .andExpect(model().attribute("receiverId", receiverId))
                .andExpect(model().attribute("receiver", receiverDto))
                .andExpect(model().attribute("messages", chatHistory));

        verify(userService, times(1)).getUserBasicDtoByEmail("sender@example.com");
        verify(userService, times(1)).getAllBasicDTOUsers();
        verify(chatService, times(1)).getChatPartners(senderId);
        verify(chatService, times(1)).getUnreadSenderIds(senderId);
        verify(userService, times(1)).findUserById(receiverId);
        verify(chatService, times(1)).getChatHistory(senderId, receiverId);
        verify(chatService, times(1)).markMessagesAsRead(receiverId, senderId);
    }

    @Test
    @WithMockUser(username = "sender@example.com")
    void sendMessage_shouldSaveAndSendMessageToUsers() {
        // given
        Long senderId = 1L;
        Long receiverId = 2L;
        String messageContent = "Test message";
        String senderEmail = "sender@example.com";
        String receiverEmail = "receiver@example.com";

        ChatMessageDTO messageDTO = new ChatMessageDTO(senderId, receiverId, messageContent, null, false);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn(senderEmail);

        when(userService.getUserEmailById(receiverId)).thenReturn(receiverEmail);

        // when
        chatController.sendMessage(messageDTO, authentication);

        // then
        ArgumentCaptor<ChatMessageDTO> messageCaptor = ArgumentCaptor.forClass(ChatMessageDTO.class);
        verify(chatService, times(1)).sendMessage(messageCaptor.capture());
        ChatMessageDTO capturedMessage = messageCaptor.getValue();
        assertNotNull(capturedMessage.getTimestamp());

        verify(messagingTemplate, times(1)).convertAndSendToUser(
                eq(receiverEmail),
                eq("/queue/messages"),
                eq(capturedMessage)
        );
        verify(messagingTemplate, times(1)).convertAndSendToUser(
                eq(senderEmail),
                eq("/queue/messages"),
                eq(capturedMessage)
        );
    }
}
