package mbds.car.pooling.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import mbds.car.pooling.dto.*;
import mbds.car.pooling.services.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import mbds.car.pooling.services.AuthService;

import org.springframework.security.core.Authentication;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final EmailService emailService;

    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> signup(
            @RequestPart("user") String userJson,
            @RequestPart(value = "justificatif", required = false) MultipartFile justificatif,
            @RequestPart(value = "pdp", required = false) MultipartFile pdp) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            SignupRequestDto request = mapper.readValue(userJson, SignupRequestDto.class);
            UserDto response = authService.signup(request, justificatif, pdp);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody SigninRequestDto request) {
        AuthResponseDto response = authService.signin(request);
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserInfo(Authentication auth) {
        String uid = auth.getName(); // UID injecté par FirebaseTokenFilter
        UserDto user = authService.getUserByUid(uid);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Refresh token manquant"));
        }
        AuthResponseDto response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-code")
    public ResponseEntity<VerificationResponseDto> verifyCode(@RequestBody VerificationRequestDto request) {
        VerificationResponseDto response = authService.verifyCode(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-code")
    public VerificationResponseDto resendCode(@RequestBody ResendCodeRequestDto request) {
        return authService.resendVerificationCode(request);
    }

    @PostMapping("/test-mail")
    public ResponseEntity<?> verifyEmail(@RequestParam String email) {
        try {
            String apiUrl = "https://api.eva.pingutil.com/email?email=" + email;
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Impossible de vérifier l'adresse email", "details", e.getMessage()));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            authService.sendReinitialisationCode(email);
            return ResponseEntity.ok("Code envoyé avec succès !");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/reset-password-code")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            String code = body.get("code");
            String newPassword = body.get("newPassword");
            authService.resetPassword(email, code, newPassword);
            return ResponseEntity.ok("Mot de passe réinitialisé avec succès !");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
