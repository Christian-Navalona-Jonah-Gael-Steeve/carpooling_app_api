package mbds.car.pooling.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;

import mbds.car.pooling.dto.SigninRequestDto;
import mbds.car.pooling.repository.UserRepository;
import mbds.car.pooling.dto.AuthResponseDto;
import mbds.car.pooling.dto.SignupRequestDto;
import mbds.car.pooling.dto.UserDto;
import mbds.car.pooling.model.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService implements IAuthService {

    @Value("${firebase.api-key}")
    private String firebaseApiKey;

    private final FirebaseApp firebaseApp;
    private final UserRepository userRepository;

    private final RestTemplate restTemplate = new RestTemplate();

    public AuthService(FirebaseApp fire_app, UserRepository userRepository) {
        this.firebaseApp = fire_app;
        this.userRepository = userRepository;
    }

    @Override
    public UserDto signup(SignupRequestDto request) throws Exception {
        // 🔹 Création sur Firebase
        UserRecord.CreateRequest firebaseRequest = new UserRecord.CreateRequest()
                .setEmail(request.getEmail())
                .setPassword(request.getPassword())
                .setDisplayName(request.getFirstName() + " " + request.getLastName())
                .setPhotoUrl(request.getPhotoUrl())
                .setPhoneNumber(request.getPhoneNumber())
                .setDisabled(request.isDisabled());

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(firebaseRequest);

        // 🔹 Sauvegarde dans PostgreSQL
        User user = new User(
                userRecord.getUid(),
                userRecord.getEmail(),
                request.getFirstName(),
                request.getLastName(),
                request.getPhoneNumber(),
                request.getCinNumber(),
                request.getRoles()
        );
        userRepository.save(user);

        // 🔹 Ensuite connexion automatique
        return getUserByUid(user.getUid());
    }

    @Override
    public AuthResponseDto signin(SigninRequestDto request) {
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + firebaseApiKey;

        Map<String, Object> body = new HashMap<>();
        body.put("email", request.getEmail());
        body.put("password", request.getPassword());
        body.put("returnSecureToken", true);

        Map<String, Object> response = restTemplate.postForObject(url, body, Map.class);

        if (response == null || !response.containsKey("idToken")) {
            return null;
        }

        AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.setAccessToken((String) response.get("idToken"));       // 🔹 token d’accès
        authResponseDto.setRefreshToken((String) response.get("refreshToken")); // 🔹 refresh token

        return authResponseDto;
    }

    @Override
    public UserDto getUserByUid(String uid) {
        return userRepository.findById(uid)
                .map(user -> {
                    UserDto dto = new UserDto();
                    dto.setUid(user.getUid());
                    dto.setEmail(user.getEmail());
                    dto.setFirstName(user.getFirstName());
                    dto.setLastName(user.getLastName());
                    dto.setPhoneNumber(user.getPhoneNumber());
                    dto.setCinNumber(user.getCinNumber());
                    dto.setRoles(user.getRoles());
                    return dto;
                })
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    @Override
    public AuthResponseDto refreshToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new IllegalArgumentException("Refresh token is missing");
        }

        String url = "https://securetoken.googleapis.com/v1/token?key=" + firebaseApiKey;

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("refresh_token", refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        Map<String, Object> response = restTemplate.postForObject(url, request, Map.class);
        if (response == null || response.get("id_token") == null) {
            throw new RuntimeException("Invalid refresh token or Firebase response error");
        }

        AuthResponseDto authResponseDto = new AuthResponseDto();
        authResponseDto.setAccessToken((String) response.get("id_token"));        // 🔹 nouveau token d’accès
        authResponseDto.setRefreshToken((String) response.get("refresh_token"));  // 🔹 nouveau refresh token

        return authResponseDto;
    }

}
