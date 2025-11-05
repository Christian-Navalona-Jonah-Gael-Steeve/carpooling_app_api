package mbds.car.pooling.repositories;

import mbds.car.pooling.entities.RideRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RideRequestRepository extends JpaRepository<RideRequest, UUID> { }
