package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.dto.ChatMessageDTO;
import com.monika.worek.orchestra.dto.UserBasicDTO;
import com.monika.worek.orchestra.service.ChatService;
import com.monika.worek.orchestra.service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService, UserService userService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping({"", "/", "/{receiverId}"})
    public String chatPage(@PathVariable(required = false) Long receiverId, Model model, Authentication authentication) {
        UserBasicDTO sender = userService.getUserBasicDtoByEmail(authentication.getName());
        Long senderId = sender.getId();

        model.addAttribute("senderId", senderId);
        model.addAttribute("users", userService.getAllBasicDTOUsers());
        model.addAttribute("chatPartners", chatService.getChatPartners(senderId));
        model.addAttribute("unreadFrom", chatService.getUnreadSenderIds(senderId));

        if (receiverId != null) {
            UserBasicDTO receiver = userService.findUserById(receiverId);
            List<ChatMessageDTO> messages = chatService.getChatHistory(senderId, receiverId);
            chatService.markMessagesAsRead(receiverId, senderId);

            model.addAttribute("receiverId", receiverId);
            model.addAttribute("receiver", receiver);
            model.addAttribute("messages", messages);
        } else {
            model.addAttribute("receiverId", null);
            model.addAttribute("receiver", new UserBasicDTO());
            model.addAttribute("messages", Collections.emptyList());
        }

        return "chat";
    }

    @MessageMapping("/chat")
    public void sendMessage(ChatMessageDTO messageDTO, Authentication authentication) {
        if (messageDTO.getTimestamp() == null) {
            messageDTO.setTimestamp(LocalDateTime.now());
        }
        chatService.sendMessage(messageDTO);
        messagingTemplate.convertAndSendToUser(
                userService.getUserEmailById(messageDTO.getReceiverId()),
                "/queue/messages",
                messageDTO
        );

        messagingTemplate.convertAndSendToUser(
                authentication.getName(),
                "/queue/messages",
                messageDTO
        );
    }
}