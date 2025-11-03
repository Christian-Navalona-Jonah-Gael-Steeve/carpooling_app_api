package mbds.car.pooling.services;

import mbds.car.pooling.dto.ChatMessageDto;
import mbds.car.pooling.dto.PrivateMessageDto;
import mbds.car.pooling.entities.Conversation;
import mbds.car.pooling.enums.MessageStatus;

/**
 * Service interface for messages
 */
public interface ChatService {

    ChatMessageDto save(ChatMessageDto message);


    ChatMessageDto update(ChatMessageDto message);

    /**
     * Send a private message
     *
     * @param privateMessage
     * @return saved message
     */
    PrivateMessageDto sendPrivateMessage(PrivateMessageDto privateMessage);

    /**
     * Update message status (SENT, DELIVERED, READ)
     *
     * @param messageId
     * @param status
     * @return updated message
     */
    PrivateMessageDto updateMessageStatus(Long messageId, MessageStatus status);

    /**
     * Find or create 1:1 conversation
     *
     * @param userId1
     * @param userId2
     * @return conversation
     */
    Conversation findOrCreateConversation(String userId1, String userId2);
}
