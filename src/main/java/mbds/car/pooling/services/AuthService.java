package mbds.car.pooling.services;

import mbds.car.pooling.dto.*;

public interface AuthService {

    UserDto signup(SignupRequestDto request) throws Exception;

    AuthResponseDto signin(SigninRequestDto request);

    UserDto getUserByUid(String uid);

    AuthResponseDto refreshToken(String refreshToken);

    VerificationResponseDto verifyCode(VerificationRequestDto request);

    VerificationResponseDto resendVerificationCode(ResendCodeRequestDto request);
}
