package mbds.car.pooling.dto;
// TripResponse.java
import mbds.car.pooling.entities.Trip;
import mbds.car.pooling.entities.User;
import org.locationtech.jts.geom.Coordinate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public record TripResponse(
        UUID id,
        String title,
        Integer seats,
        OffsetDateTime departureAt,
        OffsetDateTime arrivalAt,
        LatLngDto start,
        LatLngDto end,
        List<LatLngDto> path,
        User driver
) {
    public static TripResponse from(Trip t) {
        var start = new LatLngDto(t.getStartPoint().getY(), t.getStartPoint().getX()); // (lat=y, lng=x)
        var end   = new LatLngDto(t.getEndPoint().getY(), t.getEndPoint().getX());
        List<LatLngDto> path = Arrays.stream(t.getRoute().getCoordinates())
                .map(c -> new LatLngDto(c.getY(), c.getX()))
                .toList();
        return new TripResponse(
                t.getId(), t.getTitle(), t.getSeats(), t.getDepartureAt(), t.getArrivalAt(),
                start, end, path, t.getDriver()
        );
    }
}

