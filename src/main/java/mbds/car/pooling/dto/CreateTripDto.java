package mbds.car.pooling.dto;

import java.time.OffsetDateTime;

public record CreateTripDto(
        String driverId,
        String title,
        LatLngDto start,
        LatLngDto end,
        double[][] path,      // [[lng,lat], ...]
        Integer seats,
        OffsetDateTime departureAt,
        Integer immediateInMinutes
) {}
