package mbds.car.pooling.dto;

import lombok.Data;

@Data
public class VerificationRequestDto {
    private String email;
    private String code;
}