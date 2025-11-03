package mbds.car.pooling.dto;

public record TripMatchResponse(
        TripResponse trip,
        double startDist, // mètres
        double endDist    // mètres

) {}