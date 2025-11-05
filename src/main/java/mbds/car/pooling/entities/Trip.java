package mbds.car.pooling.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter
@Entity @Table(name = "trips")
public class Trip {
    @Id @GeneratedValue
    private UUID id;


    @ManyToOne
    @JoinColumn(name = "driver_id")
    private User driver;
    private String title;

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point startPoint;

    @Column(columnDefinition = "geometry(Point,4326)")
    private Point endPoint;

    @Column(columnDefinition = "geometry(LineString,4326)")
    private LineString route;

    @Enumerated(EnumType.STRING) @Column(nullable=false, length=16)
    private Status status = Status.ACTIVE;

    private Integer seats;
    private OffsetDateTime departureAt;
    private OffsetDateTime arrivalAt;
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public enum Status { ACTIVE, CLOSED, COMPLETED }
}
