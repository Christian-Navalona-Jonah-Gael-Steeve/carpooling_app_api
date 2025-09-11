package mbds.car.pooling.service;

import mbds.car.pooling.dto.AuthResponseDto;
import mbds.car.pooling.dto.SigninRequestDto;
import mbds.car.pooling.dto.SignupRequestDto;
import mbds.car.pooling.dto.UserDto;

public interface IAuthService {

    UserDto signup(SignupRequestDto request) throws Exception;

    AuthResponseDto signin(SigninRequestDto request);

    UserDto getUserByUid(String uid);

    AuthResponseDto refreshToken(String refreshToken);
}
