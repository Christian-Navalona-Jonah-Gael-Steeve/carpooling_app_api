package mbds.car.pooling.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import mbds.car.pooling.enums.RideRequestStatus;
import org.locationtech.jts.geom.Point;


import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name="ride_requests")
@Getter @Setter
public class RideRequest {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @ManyToOne
    @JoinColumn(name = "requester_id")
    private User user;
    @Enumerated(EnumType.STRING) @Column(nullable=false)
    private RideRequestStatus status = RideRequestStatus.PENDING;

    @Column(columnDefinition = "geometry(Point,4326)") private Point userStart;
    @Column(columnDefinition = "geometry(Point,4326)") private Point userEnd;

    private OffsetDateTime createdAt = OffsetDateTime.now();

}
