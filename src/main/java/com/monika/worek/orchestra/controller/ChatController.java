package com.monika.worek.orchestra.controller;

import com.monika.worek.orchestra.dto.UserBasicDTO;
import com.monika.worek.orchestra.model.ChatMessage;
import com.monika.worek.orchestra.service.ChatService;
import com.monika.worek.orchestra.service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;
    private final UserService userService;

    public ChatController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    @GetMapping
    public String selectUser(Model model) {
        List<UserBasicDTO> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "chatSelect";
    }

    @GetMapping("/{receiverId}")
    public String chatRoom(@PathVariable Long receiverId, Model model, Authentication authentication) {
        UserBasicDTO userDTO = userService.getUserBasicDtoByEmail(authentication.getName());
        Long senderId = userDTO.getId();

        UserBasicDTO receiver = userService.findUserById(receiverId);

        List<ChatMessage> messages = chatService.getChatHistory(senderId, receiverId);
        model.addAttribute("messages", messages);
        model.addAttribute("senderId", senderId);
        model.addAttribute("receiverId", receiverId);
        model.addAttribute("receiver", receiver);

        return "chat";
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(ChatMessage message) {
        chatService.sendMessage(message.getSenderId(), message.getReceiverId(), message.getMessageContent());
        return message;
    }
}