package mbds.car.pooling.repositories;

import mbds.car.pooling.entities.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for ChatMessage entity
 */
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * Find messages in a conversation
     *
     * @param conversationId the conversation ID
     * @param pageable
     * @return list of messages in the conversation
     */
    @Query("""
        SELECT cm FROM ChatMessage cm
        WHERE cm.conversation.id = :conversationId
        ORDER BY cm.sentAt DESC
        """)
    List<ChatMessage> findByConversationId(@Param("conversationId") Long conversationId, Pageable pageable);

    /**
     * Find latest messages for multiple conversations
     *
     * @param conversationIds list of conversation IDs
     * @return list of latest messages
     */
    @Query("""
        SELECT cm FROM ChatMessage cm
        WHERE cm.conversation.id IN :conversationIds
            AND cm.sentAt = (
                SELECT MAX(cm2.sentAt)
                FROM ChatMessage cm2
                WHERE cm2.conversation.id = cm.conversation.id
            )
        """)
    List<ChatMessage> findLatestMessagesByConversationIds(@Param("conversationIds") List<Long> conversationIds);
}
