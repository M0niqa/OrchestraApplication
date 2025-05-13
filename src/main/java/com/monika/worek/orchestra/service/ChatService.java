package com.monika.worek.orchestra.service;

import com.monika.worek.orchestra.auth.MessageMapper;
import com.monika.worek.orchestra.dto.ChatMessageDTO;
import com.monika.worek.orchestra.dto.UserBasicDTO;
import com.monika.worek.orchestra.model.ChatMessage;
import com.monika.worek.orchestra.repository.ChatRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserService userService;

    public ChatService(ChatRepository chatRepository, UserService userService) {
        this.chatRepository = chatRepository;
        this.userService = userService;
    }

    public void sendMessage(ChatMessageDTO messageDTO) {
        ChatMessage message = MessageMapper.mapToEntity(messageDTO);
        chatRepository.save(message);
    }

    public List<ChatMessageDTO> getChatHistory(Long senderId, Long receiverId) {
        return chatRepository.findChatMessagesBySenderAndReceiver(senderId, receiverId)
                .stream().map(MessageMapper::mapToDto).collect(Collectors.toList());
    }

    public List<UserBasicDTO> getChatPartners(Long userId) {
        List<Long> partnerIds = chatRepository.findChatPartnerIds(userId);
        return partnerIds.stream()
                .map(userService::findUserById)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markMessagesAsRead(Long senderId, Long receiverId) {
        chatRepository.markMessagesAsRead(senderId, receiverId);
    }

    public Set<Long> getUnreadSenderIds(Long receiverId) {
        return new HashSet<>(chatRepository.findUnreadSenderIds(receiverId));
    }

    public boolean areUnreadMessages(Long receiverId) {
        return chatRepository.existsUnreadByReceiverId(receiverId);
    }
}
