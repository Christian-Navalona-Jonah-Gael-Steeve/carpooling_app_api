package mbds.car.pooling.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import mbds.car.pooling.models.AuthResponse;
import mbds.car.pooling.models.SigninRequest;
import mbds.car.pooling.models.SignupRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Value("${firebase.api-key}")
    private String firebaseApiKey;

    private FirebaseApp firebaseApp;

    public AuthService(FirebaseApp fire_app ) {
        this.firebaseApp = fire_app;
    }

    public AuthResponse signup(SignupRequest request) throws Exception {
        UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                .setEmail(request.getEmail())
                .setPassword(request.getPassword());

        if (request.getDisplayName() != null && !request.getDisplayName().isEmpty()) {
            createRequest.setDisplayName(request.getDisplayName());
        }
        if (request.getPhotoUrl() != null && !request.getPhotoUrl().isEmpty()) {
            createRequest.setPhotoUrl(request.getPhotoUrl());
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
            createRequest.setPhoneNumber(request.getPhoneNumber());
        }
        createRequest.setDisabled(request.isDisabled());

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(createRequest);
        // return "User created with UID: " + userRecord.getUid();

        return signin(new SigninRequest(request.getEmail(), request.getPassword()));
    }

    public AuthResponse signin(SigninRequest request) {
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + firebaseApiKey;

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> body = new HashMap<>();
        body.put("email", request.getEmail());
        body.put("password", request.getPassword());
        body.put("returnSecureToken", true);

        Map<String, Object> response = restTemplate.postForObject(url, body, Map.class);

        if (response == null || !response.containsKey("idToken")) {
            return null;
        }

        AuthResponse authResponse = new AuthResponse();
        authResponse.setIdToken((String) response.get("idToken"));
        authResponse.setRefreshToken((String) response.get("refreshToken"));
        authResponse.setExpiresIn(Long.parseLong((String) response.get("expiresIn")));
        authResponse.setEmail((String) response.get("email"));
        authResponse.setUid((String) response.get("localId"));

        try {
            UserRecord user = FirebaseAuth.getInstance().getUser(authResponse.getUid());
            authResponse.setDisplayName(user.getDisplayName());
            authResponse.setPhotoUrl(user.getPhotoUrl());
            authResponse.setPhoneNumber(user.getPhoneNumber());
        } catch (Exception ignored) {
        }

        return authResponse;
    }
}
