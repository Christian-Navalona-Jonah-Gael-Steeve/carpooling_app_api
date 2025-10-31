package mbds.car.pooling.services.impl;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;
import mbds.car.pooling.dto.AuthResponseDto;
import mbds.car.pooling.dto.SigninRequestDto;
import mbds.car.pooling.dto.SignupRequestDto;
import mbds.car.pooling.dto.UserDto;
import mbds.car.pooling.entities.User;
import mbds.car.pooling.repositories.UserRepository;
import mbds.car.pooling.services.AuthService;
import mbds.car.pooling.services.FileStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class FileStorageServiceImpl implements FileStorageService {

}
