package mbds.car.pooling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mbds.car.pooling.enums.MessageStatus;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationMessageDto {
    private Long id;
    private Long conversationId;
    private UserDto sender;
    private String content;
    private Instant sentAt;
    private Instant deliveredAt;
    private Instant readAt;
    private MessageStatus status;
}
