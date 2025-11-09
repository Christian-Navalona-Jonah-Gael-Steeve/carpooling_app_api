package mbds.car.pooling.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mbds.car.pooling.entities.ChatMessage;
import mbds.car.pooling.entities.Conversation;
import mbds.car.pooling.entities.User;
import mbds.car.pooling.enums.ConversationType;
import mbds.car.pooling.enums.MessageStatus;
import mbds.car.pooling.repositories.ChatMessageRepository;
import mbds.car.pooling.repositories.ConversationRepository;
import mbds.car.pooling.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;

    private static final String USER_1_UID = "f4f15a37-cd56-4e42-ba53-22dc7c530962";
    private static final String USER_2_UID = "dd32537f-57ed-46f5-bde2-0e1bb3f9af9f";

    @Override
    public void run(String... args) {
        log.info("Starting data initialization...");

        if (conversationRepository.count() > 0) {
            log.info("Data already exists. Skipping initialization.");
            return;
        }

        try {
            initializeDemoData();
            log.info("Data initialization completed successfully.");
        } catch (Exception e) {
            log.error("Error during data initialization", e);
        }
    }

    private void initializeDemoData() {
        User user1 = userRepository.findById(USER_1_UID).orElse(null);
        User user2 = userRepository.findById(USER_2_UID).orElse(null);

        if (user1 == null || user2 == null) {
            log.warn("Users not found in database. Demo conversation data will not be initialized.");
            log.warn("User 1 ({}): {}", USER_1_UID, user1 != null ? "Found" : "Not found");
            log.warn("User 2 ({}): {}", USER_2_UID, user2 != null ? "Found" : "Not found");
            return;
        }

        Conversation conversation = new Conversation();
        conversation.setType(ConversationType.ONE_TO_ONE);
        conversation.setGroupName(null);
        conversation.setCreatedAt(parseInstant("2025-01-22T08:30:00"));
        conversation.setUpdatedAt(parseInstant("2025-01-22T10:00:01"));
        conversation.setLastMessageAt(parseInstant("2025-01-22T10:00:01"));

        Set<User> participants = new HashSet<>();
        participants.add(user1);
        participants.add(user2);
        conversation.setParticipants(participants);

        conversation = conversationRepository.save(conversation);
        log.info("Created conversation with ID: {}", conversation.getId());

        createChatMessage(conversation, user1,
            "Salut ! Je vois que tu proposes un covoiturage vers Antananarivo demain. Y a-t-il encore de la place ?",
            "2025-01-22T08:30:00", "2025-01-22T08:30:05", "2025-01-22T08:32:00", MessageStatus.READ);

        createChatMessage(conversation, user2,
            "Bonjour ! Oui il reste encore 2 places. Le départ est prévu à 7h du matin depuis Antsirabe. Ça te convient ?",
            "2025-01-22T08:35:00", "2025-01-22T08:35:02", "2025-01-22T08:37:00", MessageStatus.READ);

        createChatMessage(conversation, user1,
            "Parfait ! 7h ça me va très bien. Quel est le tarif pour le trajet ?",
            "2025-01-22T08:40:00", "2025-01-22T08:40:03", "2025-01-22T08:42:00", MessageStatus.READ);

        createChatMessage(conversation, user2,
            "Le tarif est de 15 000 Ar par personne. Le trajet dure environ 3h. Tu peux m'envoyer ton numéro pour que je te contacte si besoin ?",
            "2025-01-22T08:45:00", "2025-01-22T08:45:01", "2025-01-22T08:47:00", MessageStatus.READ);

        createChatMessage(conversation, user1,
            "Mon numéro : 032 12 345 67. Je confirme ma réservation pour demain 7h. Où exactement est le point de rencontre à Antsirabe ?",
            "2025-01-22T08:50:00", "2025-01-22T08:50:04", "2025-01-22T08:52:00", MessageStatus.READ);

        createChatMessage(conversation, user2,
            "Super ! Le point de rencontre est devant la gare routière d'Antsirabe. Je conduise une Toyota Corolla blanche, immatriculation 1234 TAA. À demain !",
            "2025-01-22T08:55:00", "2025-01-22T08:55:02", "2025-01-22T08:57:00", MessageStatus.READ);

        createChatMessage(conversation, user1,
            "Parfait, j'ai noté toutes les infos. À demain 7h devant la gare routière. Merci !",
            "2025-01-22T09:00:00", "2025-01-22T09:00:01", "2025-01-22T09:02:00", MessageStatus.READ);

        createChatMessage(conversation, user2,
            "Salut ! J'espère que le voyage s'est bien passé hier. J'organise un autre covoiturage vendredi vers Fianarantsoa, ça t'intéresse ?",
            "2025-01-22T10:00:00", "2025-01-22T10:00:01", null, MessageStatus.DELIVERED);

        log.info("Created {} chat messages", chatMessageRepository.count());
    }

    private void createChatMessage(Conversation conversation, User sender, String content,
                                   String sentAt, String deliveredAt, String readAt, MessageStatus status) {
        ChatMessage message = new ChatMessage();
        message.setConversation(conversation);
        message.setSender(sender);
        message.setContent(content);
        message.setSentAt(parseInstant(sentAt));
        message.setDeliveredAt(deliveredAt != null ? parseInstant(deliveredAt) : null);
        message.setReadAt(readAt != null ? parseInstant(readAt) : null);
        message.setStatus(status);

        chatMessageRepository.save(message);
    }

    private Instant parseInstant(String dateTimeStr) {
        String normalized = dateTimeStr.replace(" ", "T");
        return LocalDateTime.parse(normalized).toInstant(ZoneOffset.UTC);
    }
}
