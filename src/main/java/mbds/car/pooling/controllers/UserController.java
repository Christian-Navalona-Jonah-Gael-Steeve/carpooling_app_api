package mbds.car.pooling.controllers;

import lombok.RequiredArgsConstructor;
import mbds.car.pooling.dto.UserDto;
import mbds.car.pooling.entities.User;
import mbds.car.pooling.mappers.UserMapper;
import mbds.car.pooling.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for User operations
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Get user by ID
     *
     * @param userId
     * @return
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        UserDto userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);
    }
}
