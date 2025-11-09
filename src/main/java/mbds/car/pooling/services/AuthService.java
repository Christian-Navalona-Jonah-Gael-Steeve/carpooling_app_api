package mbds.car.pooling.services;

import com.google.firebase.auth.FirebaseAuthException;
import mbds.car.pooling.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AuthService {

    UserDto signup(SignupRequestDto request, MultipartFile justificatif, MultipartFile pdp) throws IOException;

    AuthResponseDto signin(SigninRequestDto request);

    UserDto getUserByUid(String uid);

    AuthResponseDto refreshToken(String refreshToken);

    VerificationResponseDto verifyCode(VerificationRequestDto request);

    VerificationResponseDto resendVerificationCode(ResendCodeRequestDto request);

    void resetPassword(String email, String code, String newPassword) throws Exception;
    void sendReinitialisationCode(String to) throws Exception;
}
