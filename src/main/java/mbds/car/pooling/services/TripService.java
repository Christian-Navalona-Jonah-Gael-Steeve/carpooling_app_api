package mbds.car.pooling.services;

import mbds.car.pooling.dto.CreateTripDto;
import mbds.car.pooling.dto.SearchTripsDto;
import mbds.car.pooling.dto.TripMatchResponse;
import mbds.car.pooling.dto.TripResponse;

import java.util.List;
import java.util.UUID;

public interface TripService {
    TripResponse create(CreateTripDto dto);
    List<TripMatchResponse> search(SearchTripsDto dto);
    List<TripResponse> listActive();
    TripResponse close(UUID id);
    TripResponse complete(UUID id);
    record TripEvent(String type, TripResponse trip){}
}
