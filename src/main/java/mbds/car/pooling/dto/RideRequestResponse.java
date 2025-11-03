package mbds.car.pooling.dto;


import mbds.car.pooling.entities.RideRequest;
import mbds.car.pooling.entities.Trip;
import mbds.car.pooling.entities.User;

import java.util.UUID;

public record RideRequestResponse(UUID id, Trip trip, User user, String status) {
    public static RideRequestResponse from(RideRequest r) {
        return new RideRequestResponse(r.getId(), r.getTrip(), r.getUser(), r.getStatus().name());
    }
}
