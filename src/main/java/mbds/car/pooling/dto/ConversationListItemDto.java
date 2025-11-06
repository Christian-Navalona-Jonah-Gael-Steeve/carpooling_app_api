package mbds.car.pooling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mbds.car.pooling.enums.ConversationType;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationListItemDto {
    private Long conversationId;
    private ConversationType type;
    private String groupName;
    private List<UserDto> participants;
    private ChatMessageDto lastMessage;
    private long unreadCount;
    private Instant lastMessageAt;
}
