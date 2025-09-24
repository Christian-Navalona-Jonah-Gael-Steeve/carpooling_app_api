package mbds.car.pooling.mappers;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

/**
 * Common configuration for all MapStruct mappers
 * This configuration is shared across all mapper interfaces in the project
 * to ensure consistent behavior and settings
 */
@MapperConfig(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MapperConfiguration {
    // This interface serves as a common configuration for all mappers
    // Individual mappers can reference this using @Mapper(config = MapperConfiguration.class)
}