package mbds.car.pooling.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TripSearchResult(
        UUID id, String title, Integer seats, Integer priceMga, OffsetDateTime departureAt,
        String routeGeoJson,
        double startDistM, double endDistM
) {}