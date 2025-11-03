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
public class MessageStatusUpdateDto {
    private Long messageId;
    private Long conversationId;
    private MessageStatus status;
    private Instant timestamp;
}
