package mbds.car.pooling.services.impl;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import mbds.car.pooling.services.FileStorageService;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @PostConstruct
    public void init() throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            System.out.println("📂 Dossier uploads créé : " + uploadPath);
        }
    }

    public String uploadFile(MultipartFile file, String uid) throws IOException {
        Path userDir = Paths.get(uploadDir, "users", uid);
        Files.createDirectories(userDir);

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = userDir.resolve(fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String publicUrl = "/users/" + uid + "/" + fileName;
        System.out.println("✅ Fichier uploadé : " + filePath);
        return publicUrl;
    }

//  public String uploadFile(MultipartFile file, String uid) throws IOException {
//        // Récupération du bucket
//        Bucket bucket = StorageClient.getInstance().bucket();
//
//        // Nom du fichier dans le bucket
//        String fileName = "users/" + uid + "/" + file.getOriginalFilename();
//
//        // Upload
//        Blob blob = bucket.create(fileName, file.getBytes(), file.getContentType());
//
//        // Générer un lien public
//        blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
//        String publicUrl = "https://storage.googleapis.com/" + bucket.getName() + "/" + fileName;
//
//        System.out.println("✅ Fichier uploadé : " + publicUrl);
//        return publicUrl;
//    }

    public void saveFileMetadataInFirestore(String uid, String email, String fileUrl) {
        Firestore firestore = FirestoreClient.getFirestore();

        Map<String, Object> data = new HashMap<>();
        data.put("uid", uid);
        data.put("email", email);
        data.put("photoUrl", fileUrl);
        data.put("createdAt", LocalDateTime.now().toString());

        firestore.collection("users").document(uid).set(data);
        System.out.println("✅ Metadata enregistrée dans Firestore.");
    }

}
