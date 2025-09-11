package mbds.car.pooling.service;

import mbds.car.pooling.dto.AuthResponse;
import mbds.car.pooling.dto.SigninRequest;
import mbds.car.pooling.dto.SignupRequest;
import mbds.car.pooling.dto.UserDTO;

public interface IAuthService {

    UserDTO signup(SignupRequest request) throws Exception;

    AuthResponse signin(SigninRequest request);

    UserDTO getUserByUid(String uid);

    AuthResponse refreshToken(String refreshToken);
}
