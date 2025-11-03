package mbds.car.pooling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageAckDto {
    private String clientId;
    private Long messageId;
    private Long conversationId;
    private Instant timestamp;
    private boolean success;
    private String error;
}
