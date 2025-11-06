package mbds.car.pooling.services;

import mbds.car.pooling.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AuthService {

    UserDto signup(SignupRequestDto request, MultipartFile photo) throws IOException;

    AuthResponseDto signin(SigninRequestDto request);

    UserDto getUserByUid(String uid);

    AuthResponseDto refreshToken(String refreshToken);

    VerificationResponseDto verifyCode(VerificationRequestDto request);

    VerificationResponseDto resendVerificationCode(ResendCodeRequestDto request);
}
