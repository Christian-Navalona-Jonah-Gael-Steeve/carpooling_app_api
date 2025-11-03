package mbds.car.pooling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mbds.car.pooling.enums.MessageUpdateType;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationUpdateDto {
    private Long conversationId;
    private PrivateMessageDto lastMessage;
    private Instant lastMessageAt;
    private MessageUpdateType updateType;
}
