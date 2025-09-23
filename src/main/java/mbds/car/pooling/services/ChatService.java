package mbds.car.pooling.services;

import mbds.car.pooling.dto.ChatMessageDto;
import mbds.car.pooling.entities.ChatMessage;

import java.util.List;

public interface ChatService {
    ChatMessageDto save(ChatMessageDto message);
    ChatMessageDto update(ChatMessageDto message);
    List<ChatMessageDto> getChatHistory(String userId1, String userId2);
}
