package mbds.car.pooling.services.impl;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;

import jakarta.transaction.Transactional;
import mbds.car.pooling.dto.*;
import mbds.car.pooling.entities.VerificationCode;
import mbds.car.pooling.enums.AccountStatus;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

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

    public AuthServiceImpl(
            FirebaseApp fire_app,
            UserRepository userRepository,
            VerificationCodeRepository verificationCodeRepository,
            FileStorageService fileStorageService,
            EmailService emailService,
            CloudinaryService cloudinaryService) {
        this.firebaseApp = fire_app;
        this.userRepository = userRepository;
        this.verificationCodeRepository = verificationCodeRepository;
        this.fileStorageService = fileStorageService;
        this.emailService = emailService;
        this.cloudinaryService = cloudinaryService;
    }

    @Transactional
    @Override
    public UserDto signup(SignupRequestDto request, MultipartFile photo) throws IOException {
        try {
            // 1️⃣ Générer un UID unique
            String uid = UUID.randomUUID().toString();

            // 2️⃣ Upload du fichier vers Firebase Storage
            String photoUrl = null;
            System.out.println(photo);
            if (photo != null && !photo.isEmpty()) {
                System.out.println("📂 Nom du fichier reçu: " + photo.getOriginalFilename());
                photoUrl = cloudinaryService.uploadFile(photo, uid);
                System.out.println("✅ Fichier uploadé sur Cloudinary: " + photoUrl);
            }

//            fileStorageService.saveFileMetadataInFirestore(uid, request.getEmail(), photoUrl);
            System.out.println("✅ Utilisateur ajouté dans cloud messaging avec UID : " + uid  + "\n" + photoUrl);

            // 🔹 Étape 1 : Création Firebase
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
                System.out.println("✅ Firebase user created: " + userRecord.getUid());
            } catch (Exception e) {
                System.err.println("❌ Erreur Firebase: " + e.getMessage());
                throw new RuntimeException("Erreur lors de la création du compte Firebase." + e.getMessage());
            }

            // 🔹 Étape 2 : Sauvegarde PostgreSQL (avec mise à jour si déjà existant)
            User user;
            try {
                List<UserRole> roles = request.getRoles();
                Optional<User> existingUserOpt = userRepository.findByEmail(userRecord.getEmail());

                if (existingUserOpt.isPresent()) {
                    throw new RuntimeException("Cet email est déjà utilisé (PostgreSQL).");
                } else {
                    // 🆕 Nouvel utilisateur → création
                    user = new User(
                            userRecord.getUid(),
                            userRecord.getEmail(),
                            request.getFirstName(),
                            request.getLastName(),
                            request.getPhoneNumber(),
                            request.getCinNumber(),
                            request.getGender(),
                            roles,
                            AccountStatus.PENDING
                    );
                    System.out.println("✅ Nouvel utilisateur enregistré: " + user.getEmail());
                }
                userRepository.save(user);

            } catch (Exception e) {
                System.err.println("❌ Erreur PostgreSQL: " + e.getMessage());
                throw new RuntimeException("Erreur lors de l’enregistrement dans PostgreSQL.", e);
            }
            // 🔹 Étape 3 : Génération + sauvegarde du code
            String code;
            try {
                code = CodeGenerator.generateCode();

                // ✅ Supprimer tous les anciens codes pour cet email avant d’enregistrer le nouveau
                verificationCodeRepository.deleteByEmail(request.getEmail());

                VerificationCode verificationCode = new VerificationCode();
                verificationCode.setEmail(user.getEmail());
                verificationCode.setCode(code);
                verificationCode.setExpiresAt(LocalDateTime.now().plusMinutes(15));

                verificationCodeRepository.save(verificationCode);

                System.out.println("✅ Code de vérification généré: " + code);
            } catch (Exception e) {
                System.err.println("❌ Erreur génération/sauvegarde code: " + e.getMessage());
                throw new RuntimeException("Erreur lors de la génération du code de vérification.", e);
            }

            // 🔹 Étape 4 : Envoi de mail
            try {
                emailService.sendVerificationCode(user.getEmail(), code);
                System.out.println("✅ Email de vérification envoyé à " + user.getEmail());
            } catch (Exception e) {
                System.err.println("❌ Erreur envoi email: " + e.getMessage());
                throw new RuntimeException("Erreur lors de l’envoi de l’email de vérification.", e);
            }

            // 🔹 Étape 5 : Retour
            return getUserByUid(user.getUid());

        } catch (Exception e) {
            System.err.println("🚨 Erreur globale dans signup(): " + e.getMessage());
            e.printStackTrace();
            throw e;
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
            emailService.sendVerificationCode(user.getEmail(), code);

            return new VerificationResponseDto(true, "✅ Nouveau code de vérification envoyé !");
        } catch (Exception e) {
            e.printStackTrace();
            return new VerificationResponseDto(false, "🚨 Erreur lors de l’envoi du code : " + e.getMessage());
        }
    }

}
