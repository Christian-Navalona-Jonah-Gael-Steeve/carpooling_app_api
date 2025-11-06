package mbds.car.pooling.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

import mbds.car.pooling.dto.SigninRequestDto;
import mbds.car.pooling.repository.UserRepository;
import mbds.car.pooling.dto.AuthResponseDto;
import mbds.car.pooling.dto.SignupRequestDto;
import mbds.car.pooling.dto.UserDto;
import mbds.car.pooling.enums.UserRole;
import mbds.car.pooling.model.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AuthService implements IAuthService {

    @Value("${firebase.api-key}")
    private String firebaseApiKey;

    private final FirebaseApp firebaseApp;
    private final UserRepository userRepository;
    private final FirebaseAuth firebaseAuth;

    private final RestTemplate restTemplate = new RestTemplate();

    public AuthService(FirebaseApp firebaseApp, UserRepository userRepository) {
        this.firebaseApp = firebaseApp;
        this.userRepository = userRepository;
        this.firebaseAuth = FirebaseAuth.getInstance(firebaseApp);
        System.out.println("✅ FirebaseAuth initialisé avec l'app: " + firebaseApp.getName());
    }

    @Override
    public UserDto signup(SignupRequestDto request) throws Exception {
        System.out.println("🔄 Début de l'inscription pour: " + request.getEmail());

        try {
            if (firebaseAuth == null) {
                throw new IllegalStateException("FirebaseAuth n'est pas initialisé");
            }

            UserRecord.CreateRequest firebaseRequest = new UserRecord.CreateRequest()
                    .setEmail(request.getEmail())
                    .setPassword(request.getPassword())
                    .setDisplayName(request.getFirstName() + " " + request.getLastName());

            if (request.getPhoneNumber() != null && !request.getPhoneNumber().isEmpty()) {
                firebaseRequest.setPhoneNumber(request.getPhoneNumber());
            }
            if (request.getPhotoUrl() != null && !request.getPhotoUrl().isEmpty()) {
                firebaseRequest.setPhotoUrl(request.getPhotoUrl());
            }

            System.out.println("🔄 Création de l'utilisateur Firebase...");
            UserRecord userRecord = firebaseAuth.createUser(firebaseRequest);
            System.out.println("✅ Utilisateur Firebase créé: " + userRecord.getUid());

            // ✅ Les rôles sont déjà de type List<UserRole> (grâce à Jackson + enum)
            List<UserRole> roles = request.getRoles() != null
                    ? new ArrayList<>(request.getRoles())
                    : new ArrayList<>();

            User user = new User();
            user.setUid(userRecord.getUid());
            user.setEmail(userRecord.getEmail());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setCinNumber(request.getCinNumber());
            user.setRoles(roles);

            userRepository.save(user);
            System.out.println("✅ Utilisateur sauvegardé en base: " + user.getUid());

            return getUserByUid(user.getUid());

        } catch (FirebaseAuthException e) {
            System.err.println("❌ Erreur Firebase Auth: " + e.getMessage());
            throw new Exception("Erreur Firebase: " + e.getMessage(), e);
        } catch (Exception e) {
            System.err.println("❌ Erreur générale lors de l'inscription: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public AuthResponseDto signin(SigninRequestDto request) {
        // 🔸 CORRIGÉ : suppression de l'espace dans l'URL
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
        authResponseDto.setAccessToken((String) response.get("idToken"));
        authResponseDto.setRefreshToken((String) response.get("refreshToken"));

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
                    dto.setRoles(new ArrayList<>(user.getRoles())); // ✅ Direct copy
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
        authResponseDto.setAccessToken((String) response.get("id_token"));
        authResponseDto.setRefreshToken((String) response.get("refresh_token"));

        return authResponseDto;
    }
}