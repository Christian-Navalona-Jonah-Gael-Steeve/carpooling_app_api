package mbds.car.pooling.services;

import mbds.car.pooling.dto.ConversationListItemDto;
import mbds.car.pooling.dto.ConversationMessageDto;

import java.util.List;

/**
 * Service interface for managing conversations
 */
public interface ConversationService {

    /**
     * Get user's conversations
     *
     * @param userId the user ID
     * @param page page number (0-indexed)
     * @param size page size
     * @return list of conversation items
     */
    List<ConversationListItemDto> getUserConversations(String userId, int page, int size);

    /**
     * Get messages in a conversation
     *
     * @param conversationId the conversation ID
     * @param page page number (0-indexed)
     * @param size page size
     * @return list of messages with user information
     */
    List<ConversationMessageDto> getConversationMessages(Long conversationId, int page, int size);
}
