package mbds.car.pooling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor   
@AllArgsConstructor  
public class SigninRequest {
    private String email;
    private String password;
}
