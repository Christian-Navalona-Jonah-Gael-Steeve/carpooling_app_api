package mbds.car.pooling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mbds.car.pooling.enums.CallSignalType;
import mbds.car.pooling.enums.CallType;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallSignalDto {
    private CallSignalType type;
    private String callerId;
    private String recipientId;
    private CallType callType;
    private String callId;
    private String callerFirstName;
    private String callerLastName;
    private Map<String, Object> offer;
    private Map<String, Object> answer;
    private Map<String, Object> candidate;
}
