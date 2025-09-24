package mbds.car.pooling.mappers;

import mbds.car.pooling.dto.ConversationDto;
import mbds.car.pooling.dto.UserDto;
import mbds.car.pooling.dto.ChatMessageDto;
import org.mapstruct.Mapper;

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
}