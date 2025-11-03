package mbds.car.pooling.dto;

public record SearchTripsDto(
        LatLngDto start,
        LatLngDto end,
        Double radiusMeters,
        Double minCoverage,
        Integer limit
) {}