package mbds.car.pooling.services.impl;

import mbds.car.pooling.dto.ChatMessageDto;
import mbds.car.pooling.dto.ConversationDto;
import mbds.car.pooling.services.ChatService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatServiceImpl implements ChatService {

    @Override
    public ChatMessageDto save(ChatMessageDto message) {
        return null;
    }

    @Override
    public ChatMessageDto update(ChatMessageDto message) {
        return null;
    }

    @Override
    public List<ChatMessageDto> getChatHistory(String userId1, String userId2) {
        return List.of();
    }

    @Override
    public List<ConversationDto> getUserConversations(String userId) {
        return List.of();
    }
}
