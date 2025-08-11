package mbds.car.pooling.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("firebase/firebase-config.json");

        if (serviceAccount == null) {
            System.err.println("🔥 Fichier firebase-config.json introuvable !");
            throw new RuntimeException("Impossible de trouver firebase/firebase-config.json");
        } else {
            System.out.println("✅ Fichier firebase-config.json trouvé.");
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp app = FirebaseApp.initializeApp(options);
            System.out.println("🚀 FirebaseApp initialized: " + app.getName());
            return app;
        } else {
            FirebaseApp app = FirebaseApp.getInstance();
            System.out.println("🚀 FirebaseApp instance récupérée: " + app.getName());
            return app;
        }
    }
}
