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
public class PrivateMessageDto {
    private Long id;
    private String clientId;
    private Long conversationId;
    private String senderId;
    private String recipientId;
    private String content;
    private Instant sentAt;
    private Instant deliveredAt;
    private Instant readAt;
    private MessageStatus status;
}
