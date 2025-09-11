package mbds.car.pooling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mbds.car.pooling.enums.UserRole;

import java.util.List;

@Data
@NoArgsConstructor   // constructeur vide
@AllArgsConstructor  // constructeur avec tous les champs
public class SignupRequestDto {
    private String email;           // obligatoire
    private String password;        // obligatoire
    private String firstName;       // obligatoire
    private String lastName;        // obligatoire
    private String phoneNumber;     // optionnel
    private String cinNumber;       // optionnel
    private String photoUrl;        // optionnel
    private boolean disabled = false; // optionnel, par défaut false
    private List<UserRole> roles;   // tableau/liste de rôles
}
