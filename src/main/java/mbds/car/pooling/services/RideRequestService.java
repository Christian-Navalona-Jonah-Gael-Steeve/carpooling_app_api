package mbds.car.pooling.services;

import mbds.car.pooling.dto.CreateRideRequestDto;
import mbds.car.pooling.dto.RideRequestResponse;

import java.util.UUID;

public interface RideRequestService {
    RideRequestResponse create(CreateRideRequestDto dto);
    RideRequestResponse accept(UUID requestId);
    RideRequestResponse reject(UUID requestId);
}
