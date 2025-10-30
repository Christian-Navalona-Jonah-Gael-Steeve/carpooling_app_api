package mbds.car.pooling.repositories;

import mbds.car.pooling.entities.Conversation;
import mbds.car.pooling.interfaces.UnreadCountView;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Conversation entity with custom JPQL queries
 */
@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    /**
     * Find conversations by User Id
     *
     * @param userId the user ID to find conversations for
     * @param pageable
     * @return list of conversation IDs 
     */
    @Query("""
        SELECT c.id FROM Conversation c
        JOIN c.participants p
        WHERE p.uid = :userId
        ORDER BY c.lastMessageAt DESC NULLS LAST, c.createdAt DESC
        """)
    List<Long> findUserConversationIds(@Param("userId") String userId, Pageable pageable);

    /**
     * Find conversations by IDs
     *
     * @param conversationIds list of conversation IDs
     * @return list of conversations
     */
    @Query("""
        SELECT DISTINCT c FROM Conversation c
        LEFT JOIN FETCH c.participants
        WHERE c.id IN :conversationIds
        ORDER BY c.lastMessageAt DESC NULLS LAST, c.createdAt DESC
        """)
    List<Conversation> findByIdsWithParticipants(@Param("conversationIds") List<Long> conversationIds);

    /**
     * Find a 1:1 conversation between two users
     *
     * @param userId1
     * @param userId2
     * @return optional conversation
     */
    @Query("""
        SELECT c FROM Conversation c
        JOIN c.participants p1
        JOIN c.participants p2
        WHERE c.type = 'ONE_TO_ONE'
            AND p1.uid = :userId1
            AND p2.uid = :userId2
            AND SIZE(c.participants) = 2
        """)
    Optional<Conversation> findOneToOneConversation(@Param("userId1") String userId1, @Param("userId2") String userId2);

    /**
     * Count unread messages in a conversation
     *
     * @param conversationId the conversation ID
     * @param userId the user ID
     * @return count of unread messages
     */
    @Query("""
        SELECT COUNT(cm)
        FROM ChatMessage cm
        WHERE cm.conversation.id = :conversationId
            AND cm.sender.uid != :userId
            AND cm.readAt IS NULL
        """)
    Long countUnreadMessages(@Param("conversationId") Long conversationId, @Param("userId") String userId);

    /**
     * Count unread messages for multiple conversations
     *
     * @param conversationIds list of conversation IDs
     * @param userId the user ID
     * @return list of conversation ID and unread count pairs
     */
    @Query("""
        SELECT cm.conversation.id as conversationId, COUNT(cm) as unreadCount
        FROM ChatMessage cm
        WHERE cm.conversation.id IN :conversationIds
            AND cm.sender.uid != :userId
            AND cm.readAt IS NULL
        GROUP BY cm.conversation.id
        """)
    List<UnreadCountView> countUnreadMessagesBatch(@Param("conversationIds") List<Long> conversationIds, @Param("userId") String userId);
}
