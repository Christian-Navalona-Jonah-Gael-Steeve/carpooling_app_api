package mbds.car.pooling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import mbds.car.pooling.enums.UserRole;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {
    private String email;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private String gender;
    private String phoneNumber;
    private String cinNumber;
    private String city;
    private String codePostal;
    private String address;
    private boolean disabled = false;
    private String userType;  // ou enum UserType si tu préfères
}
