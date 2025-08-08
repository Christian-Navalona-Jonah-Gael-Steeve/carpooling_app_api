package mbds.car.pooling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
    basePackages = {"mbds.car.pooling.firebase", "mbds.car.pooling"},
    excludeFilters = @Filter(type = FilterType.REGEX, pattern = "mbds\\.car\\.pooling\\.config\\..*")
)
public class PoolingApplication {
    public static void main(String[] args) {
        SpringApplication.run(PoolingApplication.class, args);
    }
}