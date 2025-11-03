package mbds.car.pooling.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mbds.car.pooling.dto.ConversationUpdateDto;
import mbds.car.pooling.dto.MessageAckDto;
import mbds.car.pooling.dto.MessageStatusUpdateDto;
import mbds.car.pooling.dto.PrivateMessageDto;
import mbds.car.pooling.enums.MessageStatus;
import mbds.car.pooling.enums.MessageUpdateType;
import mbds.car.pooling.services.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.Instant;

/**
 * WebSocket controller for chat
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Handle private messages
     *
     * @param privateMessage the private message
     */
    @MessageMapping("/dm")
    public void handlePrivateMessage(@Payload PrivateMessageDto privateMessage) {
        try {
            log.info("Received private message from {} to {}",
                privateMessage.getSenderId(), privateMessage.getRecipientId());

            PrivateMessageDto savedMessage = chatService.sendPrivateMessage(privateMessage);

            MessageAckDto ack = MessageAckDto.builder()
                .clientId(privateMessage.getClientId())
                .messageId(savedMessage.getId())
                .conversationId(savedMessage.getConversationId())
                .timestamp(Instant.now())
                .success(true)
                .build();
            messagingTemplate.convertAndSendToUser(
                privateMessage.getSenderId(),
                "/ack",
                ack
            );

            messagingTemplate.convertAndSendToUser(
                privateMessage.getRecipientId(),
                "/private",
                savedMessage
            );

            messagingTemplate.convertAndSendToUser(
                privateMessage.getSenderId(),
                "/private",
                savedMessage
            );

            ConversationUpdateDto conversationUpdate = ConversationUpdateDto.builder()
                .conversationId(savedMessage.getConversationId())
                .lastMessage(savedMessage)
                .lastMessageAt(savedMessage.getSentAt())
                .updateType(MessageUpdateType.NEW_MESSAGE)
                .build();

            messagingTemplate.convertAndSendToUser(
                privateMessage.getSenderId(),
                "/conversation",
                conversationUpdate
            );

            messagingTemplate.convertAndSendToUser(
                privateMessage.getRecipientId(),
                "/conversation",
                conversationUpdate
            );

            log.info("Message {} sent successfully to all devices", savedMessage.getId());

        } catch (Exception e) {
            log.error("Error handling private message: {}", e.getMessage(), e);

            MessageAckDto errorAck = MessageAckDto.builder()
                .clientId(privateMessage.getClientId())
                .messageId(null)
                .conversationId(null)
                .timestamp(Instant.now())
                .success(false)
                .error(e.getMessage())
                .build();
            messagingTemplate.convertAndSendToUser(
                privateMessage.getSenderId(),
                "/ack",
                errorAck
            );
        }
    }

    /**
     * Handle message status updates (DELIVERED, READ)
     *
     * @param statusUpdate the status update
     */
    @MessageMapping("/message-status")
    public void handleMessageStatusUpdate(@Payload MessageStatusUpdateDto statusUpdate) {
        try {
            log.info("Updating message {} status to {}",
                statusUpdate.getMessageId(), statusUpdate.getStatus());

            PrivateMessageDto updatedMessage = chatService.updateMessageStatus(
                statusUpdate.getMessageId(),
                statusUpdate.getStatus()
            );

            MessageStatusUpdateDto statusNotification = MessageStatusUpdateDto.builder()
                .messageId(updatedMessage.getId())
                .conversationId(updatedMessage.getConversationId())
                .status(updatedMessage.getStatus())
                .timestamp(statusUpdate.getStatus() == MessageStatus.DELIVERED
                    ? updatedMessage.getDeliveredAt()
                    : updatedMessage.getReadAt())
                .build();

            messagingTemplate.convertAndSendToUser(
                updatedMessage.getSenderId(),
                "/status",
                statusNotification
            );

            messagingTemplate.convertAndSendToUser(
                updatedMessage.getRecipientId(),
                "/status",
                statusNotification
            );

            ConversationUpdateDto conversationUpdate = ConversationUpdateDto.builder()
                .conversationId(updatedMessage.getConversationId())
                .lastMessage(updatedMessage)
                .lastMessageAt(updatedMessage.getSentAt())
                .updateType(MessageUpdateType.STATUS_UPDATE)
                .build();

            messagingTemplate.convertAndSendToUser(
                updatedMessage.getSenderId(),
                "/conversation",
                conversationUpdate
            );

            messagingTemplate.convertAndSendToUser(
                updatedMessage.getRecipientId(),
                "/conversation",
                conversationUpdate
            );

            log.info("Message status updated successfully on all devices");

        } catch (Exception e) {
            log.error("Error updating message status: {}", e.getMessage(), e);
        }
    }
}
