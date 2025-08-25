package mbds.car.pooling.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;

import mbds.car.pooling.repository.UserRepository;
import mbds.car.pooling.models.AuthResponse;
import mbds.car.pooling.models.SigninRequest;
import mbds.car.pooling.models.SignupRequest;
import mbds.car.pooling.models.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    @Value("${firebase.api-key}")
    private String firebaseApiKey;

    private final FirebaseApp firebaseApp;
    private final UserRepository userRepository;

    public AuthService(FirebaseApp fire_app, UserRepository userRepository) {
        this.firebaseApp = fire_app;
        this.userRepository = userRepository;
    }

    public AuthResponse signup(SignupRequest request)
            throws Exception {
        // 🔹 Création sur Firebase (uniquement champs Firebase)
        UserRecord.CreateRequest firebaseRequest = new UserRecord.CreateRequest()
                .setEmail(request.getEmail())
                .setPassword(request.getPassword())
                .setDisplayName(request.getFirstName() + " " + request.getLastName())
                .setPhotoUrl(request.getPhotoUrl())
                .setPhoneNumber(request.getPhoneNumber())
                .setDisabled(request.isDisabled());

        // 🔹 Création de l’utilisateur sur Firebase
        UserRecord userRecord = FirebaseAuth.getInstance().createUser(firebaseRequest);

        // 🔹 Sauvegarde dans PostgreSQL
        User user = new User(
                userRecord.getUid(),
                userRecord.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                request.getPhoneNumber(),
                request.getCinNumber(),
                request.getRole());
        userRepository.save(user);

        // 🔹 Ensuite connexion automatique
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

        // 🔹 Récupération des infos PostgreSQL pour compléter AuthResponse
        Optional<User> optionalUser = userRepository.findById(authResponse.getUid());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            authResponse.setFirstName(user.getFirstName());
            authResponse.setLastName(user.getLastName());
            authResponse.setPhoneNumber(user.getPhoneNumber());
            authResponse.setCinNumber(user.getCinNumber());
        }

        return authResponse;
    }
}
