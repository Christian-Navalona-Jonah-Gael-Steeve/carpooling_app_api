package mbds.car.pooling;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.google.firebase.FirebaseApp;

@SpringBootApplication
public class PoolingApplication {

    @Bean
    public CommandLineRunner testFirebaseApp(FirebaseApp firebaseApp) {
        return args -> System.out.println("🔔 Bean FirebaseApp détecté: " + firebaseApp.getName());
    }

    public static void main(String[] args) {
        SpringApplication.run(PoolingApplication.class, args);
    }
}