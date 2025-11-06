package mbds.car.pooling.services.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import mbds.car.pooling.services.CloudinaryService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    private Cloudinary cloudinary;

    public CloudinaryServiceImpl() {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dy2j4ogxl",
                "api_key", "123992884162535",
                "api_secret", "EA6joErHHKWrbBXArZWQG6MK7Tk"
        ));
    }

    public String uploadFile(MultipartFile file, String publicId) throws IOException {
        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("public_id", publicId));
        return (String) uploadResult.get("secure_url");
    }
}

