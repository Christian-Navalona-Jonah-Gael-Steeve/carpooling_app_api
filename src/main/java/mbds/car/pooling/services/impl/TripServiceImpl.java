package mbds.car.pooling.services.impl;

// TripService.java
import lombok.RequiredArgsConstructor;
import mbds.car.pooling.dto.CreateTripDto;
import mbds.car.pooling.dto.SearchTripsDto;
import mbds.car.pooling.dto.TripResponse;
import mbds.car.pooling.dto.UserDto;
import mbds.car.pooling.entities.Trip;
import mbds.car.pooling.entities.User;
import mbds.car.pooling.repositories.TripRepository;
import mbds.car.pooling.services.AuthService;
import mbds.car.pooling.services.GeometryFactoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TripServiceImpl {

    private final TripRepository tripRepository;
    private final GeometryFactoryService geom;
    private final AuthService authService;

    public TripServiceImpl(TripRepository tripRepository, GeometryFactoryService geom, AuthService authService) {
        this.tripRepository = tripRepository;
        this.geom = geom;
        this.authService = authService;
    }

    @Transactional
    public TripResponse create(CreateTripDto dto) {
        UserDto driverDto = authService.getUserByUid(dto.driverId());

        User driver = new User();
        driver.setUid(driverDto.getUid());
        driver.setEmail(driverDto.getEmail());

        Trip t = new Trip();
        t.setDriver(driver);
        t.setTitle(dto.title());
        t.setStartPoint(geom.point(dto.start().lng(), dto.start().lat()));
        t.setEndPoint(geom.point(dto.end().lng(), dto.end().lat()));
        t.setRoute(geom.line(dto.path()));
        t.setSeats(dto.seats() == null ? 3 : dto.seats());
        t.setDepartureAt(dto.departureAt() == null ? OffsetDateTime.now().plusHours(1) : dto.departureAt());

        Trip saved = tripRepository.save(t);
        return TripResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<TripResponse> search(SearchTripsDto dto) {
        double radius = dto.radiusMeters() == null ? 30.0 : dto.radiusMeters();
        double minCov = dto.minCoverage() == null ? 0.10 : dto.minCoverage();
        int limit     = dto.limit() == null ? 20 : dto.limit();

        List<Trip> found = tripRepository.searchMatching( // <-- appel à la requête PostGIS
                dto.start().lng(), dto.start().lat(),
                dto.end().lng(),   dto.end().lat(),
                radius, minCov, limit
        );

        return found.stream().map(TripResponse::from).toList();
    }
}
