package mbds.car.pooling.services.impl;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mbds.car.pooling.dto.CreateRideRequestDto;
import mbds.car.pooling.dto.RideRequestResponse;
import mbds.car.pooling.entities.RideRequest;
import mbds.car.pooling.entities.User;
import mbds.car.pooling.enums.RideRequestStatus;
import mbds.car.pooling.repositories.RideRequestRepository;
import mbds.car.pooling.repositories.TripRepository;
import mbds.car.pooling.services.GeometryFactoryService;
import mbds.car.pooling.services.RideRequestService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RideRequestServiceImpl implements RideRequestService {
    private final RideRequestRepository repo;
    private final TripRepository tripRepo;
    private final GeometryFactoryService geom;
    private final SimpMessagingTemplate broker;

    @Transactional
    public RideRequestResponse create(CreateRideRequestDto dto) {
        var trip = tripRepo.findById(dto.tripId()).orElseThrow();
        var req = new RideRequest();
        User reqUser = new User();
        reqUser.setUid(dto.userId());
        req.setTrip(trip);
        req.setUser(reqUser);
        req.setUserStart(geom.point(dto.start().lng(), dto.start().lat()));
        req.setUserEnd(geom.point(dto.end().lng(), dto.end().lat()));
        req.setStatus(RideRequestStatus.PENDING);
        var saved = repo.save(req);

        // notifier le conducteur (destinations /user/{driverId}/queue/requests)
        broker.convertAndSendToUser(
                dto.driverId(),
                "/queue/requests",
                RideRequestResponse.from(saved)
        );
        return RideRequestResponse.from(saved);
    }

    @Transactional
    public RideRequestResponse accept(UUID requestId) {
        var r = repo.findById(requestId).orElseThrow();
        r.setStatus(RideRequestStatus.ACCEPTED);
        var saved = repo.save(r);

        // notifier l'utilisateur demandeur
        broker.convertAndSendToUser(
                r.getUser().getUid(),
                "/queue/requests",
                RideRequestResponse.from(saved)
        );
        return RideRequestResponse.from(saved);
    }

    @Transactional
    public RideRequestResponse reject(UUID requestId) {
        var r = repo.findById(requestId).orElseThrow();
        r.setStatus(RideRequestStatus.REJECTED);
        var saved = repo.save(r);

        broker.convertAndSendToUser(
                r.getUser().getUid(),
                "/queue/requests",
                RideRequestResponse.from(saved)
        );
        return RideRequestResponse.from(saved);
    }
}
