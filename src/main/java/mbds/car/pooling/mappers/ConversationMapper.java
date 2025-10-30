package mbds.car.pooling.mappers;

import mbds.car.pooling.dto.ConversationDto;
import mbds.car.pooling.dto.ConversationListItemDto;
import mbds.car.pooling.dto.ConversationMessageDto;
import mbds.car.pooling.dto.UserDto;
import mbds.car.pooling.dto.ChatMessageDto;
import mbds.car.pooling.entities.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for ConversationDto
 */
@Mapper(config = MapperConfiguration.class, uses = {UserMapper.class, ChatMessageMapper.class})
public interface ConversationMapper {

    /**
     * Creates a ConversationDto using the builder pattern
     *
     * @param recipient the conversation partner
     * @param lastMessage the last message in the conversation
     * @param unreadMessages count of unread messages
     * @return ConversationDto with mapped values
     */
    default ConversationDto createConversation(UserDto recipient, ChatMessageDto lastMessage, int unreadMessages) {
        return ConversationDto.builder()
            .recipient(recipient)
            .lastMessage(lastMessage)
            .unreadMessages(unreadMessages)
            .build();
    }

    /**
     * Maps ChatMessage entity to ConversationMessageDto
     *
     * @param chatMessage the chat message entity
     * @return ConversationMessageDto or null if chatMessage is null
     */
    @Mapping(source = "conversation.id", target = "conversationId")
    ConversationMessageDto toConversationMessageDto(ChatMessage chatMessage);
}