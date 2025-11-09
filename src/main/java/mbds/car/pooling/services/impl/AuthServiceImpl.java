package mbds.car.pooling.services.impl;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;

import jakarta.transaction.Transactional;
import mbds.car.pooling.dto.*;
import mbds.car.pooling.entities.VerificationCode;
import mbds.car.pooling.enums.AccountStatus;
import mbds.car.pooling.enums.UserRole;
import mbds.car.pooling.enums.UserRole;
import mbds.car.pooling.repositories.UserRepository;
import mbds.car.pooling.entities.User;

import mbds.car.pooling.repositories.VerificationCodeRepository;
import mbds.car.pooling.services.AuthService;
import mbds.car.pooling.services.CloudinaryService;
import mbds.car.pooling.services.EmailService;
import mbds.car.pooling.services.FileStorageService;
import mbds.car.pooling.utils.CodeGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthServiceImpl implements AuthService {

    @Value("${firebase.api-key}")
    private String firebaseApiKey;

    private final FirebaseApp firebaseApp;
    private final UserRepository userRepository;
    private final VerificationCodeRepository verificationCodeRepository;
    private final FileStorageService fileStorageService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final EmailService emailService;
    private final CloudinaryService cloudinaryService;

    private final ConcurrentHashMap<String, String> resetCodes = new ConcurrentHashMap<>();

    public AuthServiceImpl(
            FirebaseApp fire_app,
            UserRepository userRepository,
            VerificationCodeRepository verificationCodeRepository,
            FileStorageService fileStorageService,
            EmailService emailService,
            CloudinaryService cloudinaryService) {
        this.firebaseApp = fire_app;
        this.userRepository = userRepository;
        System.out.println("✅ FirebaseAuth initialisé avec l'app: " + firebaseApp.getName());
        this.verificationCodeRepository = verificationCodeRepository;
        this.fileStorageService = fileStorageService;
        this.emailService = emailService;
        this.cloudinaryService = cloudinaryService;
    }

    @Transactional
    @Override
    public UserDto signup(SignupRequestDto request, MultipartFile justificatif, MultipartFile pdp) throws IOException {
        String uid = UUID.randomUUID().toString();
        String photoUrl = null;
        String justificatifUrl = null;
        boolean firebaseCreated = false;
        try {
            // -----------------------------
            // 1️⃣ Validation préalable
            // -----------------------------
            if (request.getEmail() == null || request.getEmail().isBlank()) {
                throw new RuntimeException("Email obligatoire");
            }
            if (request.getPassword() == null || request.getPassword().length() < 6) {
                throw new RuntimeException("Mot de passe invalide");
            }
            if (request.getUserType() == null) {
                throw new RuntimeException("userType obligatoire");
            }

            // -----------------------------
            // 2️⃣ Upload fichiers sur Cloudinary
            // -----------------------------
            if (justificatif != null && !justificatif.isEmpty()) {
                justificatifUrl = cloudinaryService.uploadFile(justificatif, uid);
                System.out.println("✅ Justificatif uploadé: " + justificatifUrl);
            }
            if (pdp != null && !pdp.isEmpty()) {
                photoUrl = cloudinaryService.uploadFile(pdp, uid);
                System.out.println("✅ Photo de profil uploadée: " + photoUrl);
            }

            // -----------------------------
            // 3️⃣ Création Firebase
            // -----------------------------
            UserRecord userRecord;
            try {
                UserRecord.CreateRequest firebaseRequest = new UserRecord.CreateRequest()
                        .setUid(uid)
                        .setEmail(request.getEmail())
                        .setPassword(request.getPassword())
                        .setDisplayName(request.getFirstName() + " " + request.getLastName())
                        .setPhotoUrl(photoUrl)
                        .setPhoneNumber(request.getPhoneNumber())
                        .setDisabled(true);
                userRecord = FirebaseAuth.getInstance().createUser(firebaseRequest);
                firebaseCreated = true;
                System.out.println("✅ Firebase user créé: " + userRecord.getUid());
            } catch (Exception e) {
                throw new RuntimeException("Erreur création compte Firebase: " + e.getMessage(), e);
            }

            // -----------------------------
            // 4️⃣ Enregistrement PostgreSQL
            // -----------------------------
            List<UserRole> roles = new ArrayList<>();
            if ("DRIVER".equalsIgnoreCase(request.getUserType())) {
                roles.add(UserRole.DRIVER);
            } else {
                roles.add(UserRole.PASSENGER);
            }

            User user = new User();
            user.setUid(userRecord.getUid());
            user.setEmail(userRecord.getEmail());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setCinNumber(request.getCinNumber());
            user.setGender(request.getGender());
            user.setJustificatifUrl(justificatifUrl);
            user.setCity(request.getCity());
            user.setCodePostal(request.getCodePostal());
            user.setAddress(request.getAddress());
            user.setRoles(roles);
            user.setStatus(AccountStatus.PENDING);
            userRepository.save(user);
            System.out.println("✅ Utilisateur PostgreSQL enregistré: " + user.getEmail());

            // -----------------------------
            // 5️⃣ Génération + sauvegarde code de vérification
            // -----------------------------
            String code = CodeGenerator.generateCode();
            verificationCodeRepository.deleteByEmail(user.getEmail());
            VerificationCode verificationCode = new VerificationCode();
            verificationCode.setEmail(user.getEmail());
            verificationCode.setCode(code);
            verificationCode.setExpiresAt(LocalDateTime.now().plusMinutes(15));
            verificationCodeRepository.save(verificationCode);
            System.out.println("✅ Code vérification généré: " + code);

            // -----------------------------
            // 6️⃣ Envoi email de vérification
            // -----------------------------
            emailService.sendVerificationCodeHtml(user.getEmail(), code);
            System.out.println("✅ Email envoyé: " + user.getEmail());

            // -----------------------------
            // 7️⃣ Retour DTO utilisateur
            // -----------------------------
            return getUserByUid(user.getUid());

        } catch (Exception e) {
            System.err.println("🚨 Erreur signup(): " + e.getMessage());
            e.printStackTrace();

            // -----------------------------
            // 🔄 Rollback Firebase et Cloudinary si nécessaire
            // -----------------------------
            if (firebaseCreated) {
                try {
                    FirebaseAuth.getInstance().deleteUser(uid);
                    System.out.println("↩️ Rollback Firebase effectué pour UID: " + uid);
                } catch (Exception ex) {
                    System.err.println("❌ Erreur rollback Firebase: " + ex.getMessage());
                }
            }
            // Supprimer fichiers uploadés sur Cloudinary si besoin
            if (photoUrl != null) {
                cloudinaryService.deleteFile(photoUrl);
            }
            if (justificatifUrl != null) {
                cloudinaryService.deleteFile(justificatifUrl);
            }

            throw e; // on relance l’exception pour que Spring rollback PostgreSQL
        }
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

    @Transactional
    @Override
    public VerificationResponseDto verifyCode(VerificationRequestDto request) {
        try {
            // 🔹 Récupérer le dernier code valide pour cet email
            VerificationCode verificationCode = verificationCodeRepository
                    .findTopValidByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Aucun code trouvé pour cet email."));

            // 🔹 Vérifier le code
            if (!verificationCode.getCode().equals(request.getCode())) {
                return new VerificationResponseDto(false, "❌ Code incorrect");
            }

            // 🔹 Vérifier l'expiration
            if (verificationCode.getExpiresAt().isBefore(LocalDateTime.now())) {
                return new VerificationResponseDto(false, "⏰ Code expiré");
            }

            // 🔹 Récupérer l'utilisateur
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable."));

            // 🔹 Activer l'utilisateur
            user.setStatus(AccountStatus.ACTIVE);
            userRepository.save(user);

            // 🔹 Activer dans Firebase
            FirebaseAuth.getInstance().updateUser(
                    new UserRecord.UpdateRequest(user.getUid()).setDisabled(false)
            );

            // 🔹 Supprimer tous les codes existants pour cet email
            verificationCodeRepository.deleteByEmail(request.getEmail());
            return new VerificationResponseDto(true, "✅ Compte activé avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            return new VerificationResponseDto(false,
                    "🚨 Erreur lors de la vérification du code : " + e.getMessage());
        }
    }

    @Transactional
    @Override
    public VerificationResponseDto resendVerificationCode(ResendCodeRequestDto request) {
        try {
            // 🔹 Vérifier que l'utilisateur existe
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable."));

            // 🔹 Supprimer tous les anciens codes
            verificationCodeRepository.deleteByEmail(user.getEmail());

            // 🔹 Générer un nouveau code
            String code = CodeGenerator.generateCode();

            VerificationCode verificationCode = new VerificationCode();
            verificationCode.setEmail(user.getEmail());
            verificationCode.setCode(code);
            verificationCode.setExpiresAt(LocalDateTime.now().plusMinutes(15));
            verificationCodeRepository.save(verificationCode);

            // 🔹 Envoyer par email
            emailService.sendVerificationCodeHtml(user.getEmail(), code);

            return new VerificationResponseDto(true, "✅ Nouveau code de vérification envoyé !");
        } catch (Exception e) {
            e.printStackTrace();
            return new VerificationResponseDto(false, "🚨 Erreur lors de l’envoi du code : " + e.getMessage());
        }
    }

    public void resetPassword(String email, String code, String newPassword) throws Exception {
        String storedCode = resetCodes.get(email);
        System.out.println(storedCode);
        if (storedCode == null || !storedCode.equals(code)) {
            throw new Exception("Code de réinitialisation invalide ou expiré.");
        }

        // Met à jour le mot de passe dans Firebase
        UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(email);
        FirebaseAuth.getInstance().updateUser(
                new UserRecord.UpdateRequest(userRecord.getUid())
                        .setPassword(newPassword)
        );

        // Supprime le code après utilisation
        resetCodes.remove(email);
    }

    public void sendReinitialisationCode(String to) throws Exception {
        // Vérifie si l'utilisateur existe dans Firebase
        UserRecord user = FirebaseAuth.getInstance().getUserByEmail(to);
        if (user == null) {
            throw new Exception("Aucun compte associé à cet email.");
        }
        // Génère le code
        String code = String.format("%06d", new Random().nextInt(999999));

        System.out.println("---" + code);
        System.out.println("---" +  to);
        resetCodes.put(to, code);
        emailService.sendReinitialisationCodeHtml(to, code);
    }
}
