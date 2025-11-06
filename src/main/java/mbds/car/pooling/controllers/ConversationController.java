package mbds.car.pooling.controllers;

import mbds.car.pooling.dto.ConversationListItemDto;
import mbds.car.pooling.dto.ConversationMessageDto;
import mbds.car.pooling.services.ConversationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    /**
     * Get user conversations
     *
     * @param auth Auth
     * @param page page number (default: 0)
     * @param size page size (default: 10)
     * @return list of user conversations
     */
    @GetMapping
    public ResponseEntity<List<ConversationListItemDto>> getUserConversations(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String userId = auth.getName(); // UID from FirebaseTokenFilter
        List<ConversationListItemDto> conversations = conversationService.getUserConversations(userId, page, size);
        return ResponseEntity.ok(conversations);
    }

    /**
     * Get messages in a specific conversation
     *
     * @param conversationId the conversation ID
     * @param page           page number (default: 0)
     * @param size           page size (default: 10)
     * @return list of messages in the conversation
     */
    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<List<ConversationMessageDto>> getConversationMessages(
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<ConversationMessageDto> messages = conversationService.getConversationMessages(conversationId, page, size);
        return ResponseEntity.ok(messages);
    }
}
