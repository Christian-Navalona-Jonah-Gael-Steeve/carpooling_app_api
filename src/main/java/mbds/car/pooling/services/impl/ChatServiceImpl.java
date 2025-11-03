package mbds.car.pooling.services.impl;

import lombok.RequiredArgsConstructor;
import mbds.car.pooling.dto.ChatMessageDto;
import mbds.car.pooling.dto.PrivateMessageDto;
import mbds.car.pooling.entities.ChatMessage;
import mbds.car.pooling.entities.Conversation;
import mbds.car.pooling.entities.User;
import mbds.car.pooling.enums.ConversationType;
import mbds.car.pooling.enums.MessageStatus;
import mbds.car.pooling.repositories.ChatMessageRepository;
import mbds.car.pooling.repositories.ConversationRepository;
import mbds.car.pooling.repositories.UserRepository;
import mbds.car.pooling.services.ChatService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of ChatService
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ChatMessageDto save(ChatMessageDto message) {
        return null;
    }

    @Override
    @Transactional
    public ChatMessageDto update(ChatMessageDto message) {
        return null;
    }

    @Override
    @Transactional
    public PrivateMessageDto sendPrivateMessage(PrivateMessageDto privateMessage) {
        Conversation conversation = findOrCreateConversation(
            privateMessage.getSenderId(),
            privateMessage.getRecipientId()
        );

        User sender = userRepository.findById(privateMessage.getSenderId())
            .orElseThrow(() -> new RuntimeException("Sender not found: " + privateMessage.getSenderId()));

        ChatMessage message = new ChatMessage();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(privateMessage.getContent());
        message.setSentAt(Instant.now());
        message.setStatus(MessageStatus.SENT);

        ChatMessage savedMessage = chatMessageRepository.save(message);


        conversation.setLastMessageAt(savedMessage.getSentAt());
        conversationRepository.save(conversation);

        return mapToPrivateMessageDto(savedMessage, privateMessage.getRecipientId(), privateMessage.getClientId());
    }

    @Override
    @Transactional
    public PrivateMessageDto updateMessageStatus(Long messageId, MessageStatus status) {
        ChatMessage message = chatMessageRepository.findById(messageId)
            .orElseThrow(() -> new RuntimeException("Message not found: " + messageId));

        Instant now = Instant.now();
        message.setStatus(status);

        switch (status) {
            case DELIVERED:
                message.setDeliveredAt(now);
                break;
            case READ:
                message.setReadAt(now);
                if (message.getDeliveredAt() == null) {
                    message.setDeliveredAt(now);
                }
                break;
            default:
                break;
        }

        ChatMessage updatedMessage = chatMessageRepository.save(message);

        String recipientId = updatedMessage.getConversation().getParticipants().stream()
            .filter(user -> !user.getUid().equals(updatedMessage.getSender().getUid()))
            .findFirst()
            .map(User::getUid)
            .orElse(null);

        return mapToPrivateMessageDto(updatedMessage, recipientId, null);
    }

    @Override
    @Transactional
    public Conversation findOrCreateConversation(String userId1, String userId2) {
        return conversationRepository.findOneToOneConversation(userId1, userId2)
            .orElseGet(() -> {
                User user1 = userRepository.findById(userId1)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId1));
                User user2 = userRepository.findById(userId2)
                    .orElseThrow(() -> new RuntimeException("User not found: " + userId2));

                Conversation newConversation = new Conversation();
                newConversation.setType(ConversationType.ONE_TO_ONE);

                Set<User> participants = new HashSet<>();
                participants.add(user1);
                participants.add(user2);
                newConversation.setParticipants(participants);

                return conversationRepository.save(newConversation);
            });
    }

    /**
     * Helper map ChatMessage to PrivateMessageDto
     *
     * @param message
     * @param recipientId
     * @param clientId
     */
    private PrivateMessageDto mapToPrivateMessageDto(ChatMessage message, String recipientId, String clientId) {
        return PrivateMessageDto.builder()
            .clientId(clientId)
            .id(message.getId())
            .conversationId(message.getConversation().getId())
            .senderId(message.getSender().getUid())
            .recipientId(recipientId)
            .content(message.getContent())
            .sentAt(message.getSentAt())
            .deliveredAt(message.getDeliveredAt())
            .readAt(message.getReadAt())
            .status(message.getStatus())
            .build();
    }
}
