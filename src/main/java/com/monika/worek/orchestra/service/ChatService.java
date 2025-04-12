package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.model.ChatMessage;
import com.monika.worek.orchestra.repository.ChatRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {
    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public void sendMessage(Long senderId, Long receiverId, String messageContent) {
        ChatMessage message = new ChatMessage(senderId, receiverId, messageContent);
        chatRepository.save(message);
    }

    public List<ChatMessage> getChatHistory(Long senderId, Long receiverId) {
        List<ChatMessage> history = chatRepository.findChatMessagesBySenderAndReceiver(senderId, receiverId);
        System.out.println(history);
        return history;
    }
}
