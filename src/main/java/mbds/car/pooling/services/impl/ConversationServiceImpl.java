package mbds.car.pooling.services.impl;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import mbds.car.pooling.dto.ChatMessageDto;
import mbds.car.pooling.dto.ConversationListItemDto;
import mbds.car.pooling.dto.ConversationMessageDto;
import mbds.car.pooling.dto.UserDto;
import mbds.car.pooling.entities.ChatMessage;
import mbds.car.pooling.entities.Conversation;
import mbds.car.pooling.interfaces.UnreadCountView;
import mbds.car.pooling.mappers.ChatMessageMapper;
import mbds.car.pooling.mappers.ConversationMapper;
import mbds.car.pooling.mappers.UserMapper;
import mbds.car.pooling.repositories.ChatMessageRepository;
import mbds.car.pooling.repositories.ConversationRepository;
import mbds.car.pooling.services.ConversationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of ConversationService
 */
@Service
@Transactional(readOnly = true)
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ConversationMapper conversationMapper;
    private final ChatMessageMapper chatMessageMapper;
    private final UserMapper userMapper;

    public ConversationServiceImpl(
            ConversationRepository conversationRepository,
            ChatMessageRepository chatMessageRepository,
            ConversationMapper conversationMapper,
            ChatMessageMapper chatMessageMapper,
            UserMapper userMapper
    ) {
        this.conversationRepository = conversationRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.conversationMapper = conversationMapper;
        this.chatMessageMapper = chatMessageMapper;
        this.userMapper = userMapper;
    }

    @Override
    public List<ConversationListItemDto> getUserConversations(String userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<Long> conversationIds = conversationRepository.findUserConversationIds(userId, pageable);

        if (conversationIds.isEmpty()) {
            return List.of();
        }

        List<Conversation> conversations = conversationRepository.findByIdsWithParticipants(conversationIds);

        List<ChatMessage> latestMessages = chatMessageRepository.findLatestMessagesByConversationIds(conversationIds);
        java.util.Map<Long, ChatMessage> latestMessageMap = latestMessages.stream()
            .collect(Collectors.toMap(
                msg -> msg.getConversation().getId(),
                msg -> msg
            ));

        List<UnreadCountView> unreadCountResults = conversationRepository.countUnreadMessagesBatch(conversationIds, userId);
        java.util.Map<Long, Long> unreadCountMap = unreadCountResults.stream()
            .collect(Collectors.toMap(
                UnreadCountView::getConversationId,
                UnreadCountView::getUnreadCount
            ));

        return conversations.stream()
            .map(conversation -> {
                ChatMessage latestMessage = latestMessageMap.get(conversation.getId());

                Long unreadCount = unreadCountMap.getOrDefault(conversation.getId(), 0L);

                List<UserDto> participantDtos = conversation.getParticipants().stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());

                ChatMessageDto lastMessageDto = latestMessage != null
                    ? chatMessageMapper.toDto(latestMessage)
                    : null;

                return ConversationListItemDto.builder()
                    .conversationId(conversation.getId())
                    .type(conversation.getType())
                    .groupName(conversation.getGroupName())
                    .participants(participantDtos)
                    .lastMessage(lastMessageDto)
                    .unreadCount(unreadCount)
                    .lastMessageAt(conversation.getLastMessageAt())
                    .build();
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<ConversationMessageDto> getConversationMessages(Long conversationId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        List<ChatMessage> messages = chatMessageRepository.findByConversationId(conversationId, pageable);

        return messages.stream()
            .map(conversationMapper::toConversationMessageDto)
            .collect(Collectors.toList());
    }
}
