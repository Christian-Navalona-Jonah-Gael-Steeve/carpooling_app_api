package mbds.car.pooling.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    private String justificatifUrl;
    private String city;
    private String codePostal;
    private String address;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<UserRole> roles;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Review> receivedReviews;

    @OneToMany(mappedBy = "reviewer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Review> givenReviews;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountStatus status = AccountStatus.PENDING;

    public User(String uid, String email, String firstName, String lastName,
                String phoneNumber, String cinNumber, String gender,
                String justificatifUrl, String city, String codePostal, String address,
                List<UserRole> roles, AccountStatus status) {
        this.uid = uid;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.cinNumber = cinNumber;
        this.gender = gender;
        this.justificatifUrl = justificatifUrl;
        this.city = city;
        this.codePostal = codePostal;
        this.address = address;
        this.roles = roles;
        this.status = status;
    }
}