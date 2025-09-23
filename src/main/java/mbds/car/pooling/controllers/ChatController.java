package mbds.car.pooling.controllers;

import mbds.car.pooling.services.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/chat")
public class ChatController {

    private ChatService chatService;

    @GetMapping("/history/{userId1}/{userId2}")
    public ResponseEntity<?> getChatHistory(@PathVariable  String userId1,@PathVariable String userId2) {
        return ResponseEntity.ok(chatService.getChatHistory(userId1, userId2));
    }
}
