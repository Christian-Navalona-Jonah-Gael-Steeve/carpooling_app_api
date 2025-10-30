package mbds.car.pooling.services;

import mbds.car.pooling.dto.ChatMessageDto;

/**
 * Service interface for managing conversation messages
 */
public interface ChatService {
    /**
     * Save a new chat message
     */
    ChatMessageDto save(ChatMessageDto message);

    /**
     * Update an existing chat message
     */
    ChatMessageDto update(ChatMessageDto message);
}
