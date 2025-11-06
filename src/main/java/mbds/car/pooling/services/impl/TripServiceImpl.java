package mbds.car.pooling.services.impl;

// TripService.java
import lombok.RequiredArgsConstructor;
import mbds.car.pooling.dto.*;
import mbds.car.pooling.entities.Trip;
import mbds.car.pooling.entities.User;
import mbds.car.pooling.repositories.TripRepository;
import mbds.car.pooling.services.AuthService;
import mbds.car.pooling.services.GeometryFactoryService;
import mbds.car.pooling.utils.Postgis;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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
    private  final SimpMessagingTemplate broker;

    public TripServiceImpl(TripRepository tripRepository, GeometryFactoryService geom, AuthService authService, SimpMessagingTemplate broker) {
        this.tripRepository = tripRepository;
        this.geom = geom;
        this.authService = authService;
        this.broker = broker;
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

//        broker.convertAndSend("/topic/trips", TripResponse.from(saved));
        return TripResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<TripMatchResponse> search(SearchTripsDto dto) {
        double radius = dto.radiusMeters() == null ? 30.0 : dto.radiusMeters();
        double minCov = dto.minCoverage() == null ? 0.10 : dto.minCoverage();
        int    limit  = dto.limit() == null ? 20 : dto.limit();

        var trips = tripRepository.searchMatching(
                dto.start().lng(), dto.start().lat(),
                dto.end().lng(),   dto.end().lat(),
                radius, minCov, limit
        );

        return trips.stream().map(t -> {
//            double sd = tripRepository.distanceToRoute(t.getId(), dto.start().lng(), dto.start().lat());
//            double ed = tripRepository.distanceToRoute(t.getId(), dto.end().lng(),   dto.end().lat());
//            return new TripMatchResponse(TripResponse.from(t), sd, ed);
//        }).toList();
            double rStart = Postgis.distancePointToPointMeters(
                    t.getStartPoint(), dto.current().lng(), dto.current().lat());
            double rEnd   = Postgis.distancePointToPointMeters(
                    t.getEndPoint(), dto.end().lng(), dto.end().lat());
            return new TripMatchResponse(TripResponse.from(t), rStart, rEnd);
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<TripResponse> listActive() {
        return tripRepository.findByStatusOrderByCreatedAtDesc(Trip.Status.ACTIVE)
                .stream().map(TripResponse::from).toList();
    }
    @Transactional
    public TripResponse close(UUID id) {
        Trip t = tripRepository.findById(id).orElseThrow();
        t.setStatus(Trip.Status.CLOSED);
        Trip saved = tripRepository.save(t);
        TripResponse payload = TripResponse.from(saved);
        broker.convertAndSend("/topic/trips", new TripEvent("CLOSED", payload));
        return payload;
    }

    @Transactional
    public TripResponse complete(UUID id) {
        Trip t = tripRepository.findById(id).orElseThrow();
        t.setStatus(Trip.Status.COMPLETED);
        Trip saved = tripRepository.save(t);
        TripResponse payload = TripResponse.from(saved);
        broker.convertAndSend("/topic/trips", new TripEvent("COMPLETED", payload));
        return payload;
    }

    public record TripEvent(String type, TripResponse trip) {}
}
