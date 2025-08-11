package mbds.car.pooling.models;

import lombok.Data;

@Data
public class AuthResponse {
    private String idToken;
    private String refreshToken;
    private Long expiresIn;
    private String uid;
    private String email;
    private String displayName;
    private String photoUrl;
    private String phoneNumber;
}