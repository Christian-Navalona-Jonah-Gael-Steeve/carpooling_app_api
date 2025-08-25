package mbds.car.pooling.models;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
public class User {
    @Id
    private String uid;   // UID Firebase

    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String cinNumber;

    @Enumerated(EnumType.STRING)
    private UserRole role;


}