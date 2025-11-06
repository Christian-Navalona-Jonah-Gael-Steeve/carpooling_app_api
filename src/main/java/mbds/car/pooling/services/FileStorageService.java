package mbds.car.pooling.services;

import mbds.car.pooling.dto.AuthResponseDto;
import mbds.car.pooling.dto.SigninRequestDto;
import mbds.car.pooling.dto.SignupRequestDto;
import mbds.car.pooling.dto.UserDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileStorageService {
    String uploadFile(MultipartFile file, String uid) throws IOException;
    void saveFileMetadataInFirestore(String uid, String email, String fileUrl);
}
