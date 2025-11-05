package mbds.car.pooling.controllers;

// TripController.java
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mbds.car.pooling.dto.CreateTripDto;
import mbds.car.pooling.dto.SearchTripsDto;
import mbds.car.pooling.dto.TripMatchResponse;
import mbds.car.pooling.dto.TripResponse;
import mbds.car.pooling.services.TripService;
import mbds.car.pooling.services.impl.TripServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @PostMapping
    public TripResponse create(@RequestBody CreateTripDto dto) {
        return tripService.create(dto);
    }

    @PostMapping("/search")
    public List<TripMatchResponse> search(@RequestBody @Valid SearchTripsDto dto) {
        return tripService.search(dto);
    }

    @GetMapping public List<TripResponse> list(@RequestParam(defaultValue="ACTIVE") String status) {
        return tripService.listActive();
    }

    @PatchMapping("/{id}/close") public TripResponse close(@PathVariable UUID id) { return tripService.close(id); }
    @PatchMapping("/{id}/complete") public TripResponse complete(@PathVariable UUID id) { return tripService.complete(id); }
}
