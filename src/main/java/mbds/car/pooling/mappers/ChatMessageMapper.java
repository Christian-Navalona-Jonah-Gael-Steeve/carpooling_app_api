package mbds.car.pooling.mappers;

import mbds.car.pooling.dto.ChatMessageDto;
import mbds.car.pooling.entities.ChatMessage;
import org.mapstruct.Mapper;

/**
 * Mapper for ChatMessage
 */
@Mapper(
    config = MapperConfiguration.class,
    uses = {UserMapper.class}
)
public interface ChatMessageMapper {

    /**
     * Converts ChatMessage entity to ChatMessageDto
     *
     * @param chatMessage the chat message entity to convert
     * @return ChatMessageDto or null if chatMessage is null
     */
    ChatMessageDto toDto(ChatMessage chatMessage);

    /**
     * Converts ChatMessageDto to ChatMessage entity
     *
     * @param chatMessageDto the chat message DTO to convert
     * @return ChatMessage entity or null if chatMessageDto is null
     */
    ChatMessage toEntity(ChatMessageDto chatMessageDto);
}