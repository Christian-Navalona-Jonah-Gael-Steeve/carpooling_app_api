package mbds.car.pooling.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor   // constructeur vide
@AllArgsConstructor  // constructeur avec tous les champs
public class SigninRequest {
    private String email;
    private String password;
}
