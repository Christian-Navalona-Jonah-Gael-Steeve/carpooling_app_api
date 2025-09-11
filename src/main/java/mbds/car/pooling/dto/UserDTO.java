package mbds.car.pooling.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mbds.car.pooling.models.UserRole;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String uid;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String cinNumber;
    private List<UserRole> roles;
}