package mbds.car.pooling.controllers;

// TripController.java
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mbds.car.pooling.dto.CreateTripDto;
import mbds.car.pooling.dto.SearchTripsDto;
import mbds.car.pooling.dto.TripResponse;
import mbds.car.pooling.services.impl.TripServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripServiceImpl tripService;

    @PostMapping
    public TripResponse create(@RequestBody CreateTripDto dto) {

        System.out.println(dto.driverId());
        return tripService.create(dto);
    }

    @PostMapping("/search")
    public List<TripResponse> search(@RequestBody @Valid SearchTripsDto dto) {
        return tripService.search(dto); // <-- idem
    }
}
