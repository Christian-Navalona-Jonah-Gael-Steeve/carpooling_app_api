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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    private final EmailService emailService;
//    @PostMapping(value = "/signup", consumes = {"multipart/form-data"})
//    public ResponseEntity<?> signup(
//            @RequestPart("user") SignupRequestDto request,
//            @RequestPart(value = "photo", required = false) MultipartFile photo) {
//        try {
//            UserDto response = authService.signup(request, photo);
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
//        }
//    }

    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> signup(
            @RequestPart("user") String userJson,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            SignupRequestDto request = mapper.readValue(userJson, SignupRequestDto.class);
            UserDto response = authService.signup(request, photo);
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


}
