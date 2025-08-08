package mbds.car.pooling.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signUp(@RequestBody Map<String, String> request) {
        try {
            UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                    .setEmail(request.get("email"))
                    .setPassword(request.get("password"))
                    .setDisplayName(request.get("displayName"));
            UserRecord userRecord = FirebaseAuth.getInstance().createUser(createRequest);

            Map<String, String> response = new HashMap<>();
            response.put("uid", userRecord.getUid());
            response.put("message", "User created successfully");
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Failed to create user: " + e.getMessage()));
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<String, String>> signIn(@RequestBody Map<String, String> request) {
        // Firebase sign-in is typically handled client-side, but you can validate the token here
        try {
            String idToken = request.get("idToken");
            FirebaseAuth.getInstance().verifyIdToken(idToken);
            return ResponseEntity.ok(Map.of("message", "Sign-in successful"));
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid token: " + e.getMessage()));
        }
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, String>> getUserDetails(@RequestAttribute("uid") String uid) {
        return ResponseEntity.ok(Map.of("uid", uid));
    }
}