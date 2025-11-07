package mbds.car.pooling.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudinaryService {
    String uploadFile(MultipartFile file, String publicId) throws IOException;
    void deleteFile(String fileUrl);
}
