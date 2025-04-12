package com.monika.worek.orchestra.controller;//package com.monika.worek.orchestra.controller;
//
//import com.monika.worek.orchestra.model.ChatMessage;
//import com.monika.worek.orchestra.service.ChatService;
//import org.springframework.messaging.handler.annotation.DestinationVariable;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Controller;
//
//@Controller
//public class ChatWebSocketHandler {
//
//    private final ChatService chatService;
//    private final SimpMessagingTemplate simpMessagingTemplate;
//
//    public ChatWebSocketHandler(ChatService chatService, SimpMessagingTemplate simpMessagingTemplate) {
//        this.chatService = chatService;
//        this.simpMessagingTemplate = simpMessagingTemplate;
//    }
//
//    @MessageMapping("/chat/private/{receiverId}")
//    public void sendPrivateMessage(@DestinationVariable Long receiverId, ChatMessage message) {
//        chatService.saveMessage(message);
//        simpMessagingTemplate.convertAndSendToUser(
//                String.valueOf(receiverId),
//                "/queue/messages",
//                message
//        );
//    }
//}
