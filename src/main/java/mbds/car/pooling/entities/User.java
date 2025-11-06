package mbds.car.pooling.entities;

import jakarta.persistence.*;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import mbds.car.pooling.enums.AccountStatus;
import mbds.car.pooling.enums.UserRole;

import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String uid;   // UID Firebase

    @Column(unique = true, nullable = false)
    private String email;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String cinNumber;
    private String gender;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<UserRole> roles;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus status = AccountStatus.PENDING;

}