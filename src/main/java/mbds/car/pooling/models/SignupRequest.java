package mbds.car.pooling.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor   // constructeur vide
@AllArgsConstructor  // constructeur avec tous les champs
public class SignupRequest {
    private String email;       // obligatoire
    private String password;    // obligatoire
    private String firstName;   // obligatoire
    private String lastName;    // obligatoire
    private String phoneNumber; // optionnel
    private String cinNumber;   // optionnel
    private String photoUrl;    // optionnel
    private boolean disabled = false; // optionnel, par défaut false
    private UserRole role;
}
