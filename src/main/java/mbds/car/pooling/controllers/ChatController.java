package mbds.car.pooling.controllers;

import mbds.car.pooling.services.ChatService;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/chat")
@RestController
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }
}
