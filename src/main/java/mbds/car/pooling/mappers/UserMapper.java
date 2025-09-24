package mbds.car.pooling.mappers;

import mbds.car.pooling.dto.UserDto;
import mbds.car.pooling.entities.User;
import org.mapstruct.Mapper;

/**
 * Mapper for User
 */
@Mapper(config = MapperConfiguration.class)
public interface UserMapper {

    /**
     * Converts User entity to UserDto
     *
     * @param user the user entity to convert
     * @return UserDto or null if user is null
     */
    UserDto toDto(User user);

    /**
     * Converts UserDto to User entity
     *
     * @param userDto the user DTO to convert
     * @return User entity or null if userDto is null
     */
    User toEntity(UserDto userDto);
}