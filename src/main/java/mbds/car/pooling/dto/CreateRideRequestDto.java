package mbds.car.pooling.dto;

import java.util.UUID;

public record CreateRideRequestDto(UUID tripId, LatLngDto start, LatLngDto end, String driverId, String userId) {}