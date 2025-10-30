package mbds.car.pooling.services.impl;

import mbds.car.pooling.dto.ChatMessageDto;
import mbds.car.pooling.services.ChatService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of ChatService
 */
@Service
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService {

    public ChatServiceImpl() {}

    @Override
    @Transactional
    public ChatMessageDto save(ChatMessageDto message) {
        return null;
    }

    @Override
    @Transactional
    public ChatMessageDto update(ChatMessageDto message) {
        return null;
    }
}
