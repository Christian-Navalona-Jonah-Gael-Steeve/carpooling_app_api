package mbds.car.pooling.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mbds.car.pooling.dto.CreateRideRequestDto;
import mbds.car.pooling.dto.RideRequestResponse;
import mbds.car.pooling.services.RideRequestService;
import mbds.car.pooling.services.impl.RideRequestServiceImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class RideRequestController {
    private final RideRequestService service;

    @PostMapping
    public RideRequestResponse create(@RequestBody @Valid CreateRideRequestDto dto) {
        return service.create(dto);
    }

    @PostMapping("/{id}/accept")
    public RideRequestResponse accept(@PathVariable UUID id) {
        return service.accept(id);
    }

    @PostMapping("/{id}/reject")
    public RideRequestResponse reject(@PathVariable UUID id) {
        return service.reject(id);
    }
}