package mbds.car.pooling.repositories;

import mbds.car.pooling.entities.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, UUID> {
    List<Trip> findByStatusOrderByCreatedAtDesc(Trip.Status status);

    @Query(value = """
      WITH params AS (
        SELECT ST_SetSRID(ST_MakePoint(:startLng,:startLat),4326) AS u_start,
               ST_SetSRID(ST_MakePoint(:endLng,:endLat),4326)     AS u_end
      ),
      cand AS (
        SELECT t.*
        FROM trips t, params p
        WHERE t.status='ACTIVE' 
           AND   ST_DWithin(t.route::geography, p.u_start::geography, :radius)
          AND ST_DWithin(t.route::geography, p.u_end::geography,   :radius)
          AND ST_LineLocatePoint(t.route, p.u_end) > ST_LineLocatePoint(t.route, p.u_start)
          AND (ST_LineLocatePoint(t.route, p.u_end) - ST_LineLocatePoint(t.route, p.u_start)) >= :minCov
      )
      SELECT * FROM cand
      ORDER BY created_at DESC
      LIMIT :limit
      """, nativeQuery = true)
        List<Trip> searchMatching(
                @Param("startLng") double startLng, @Param("startLat") double startLat,
                @Param("endLng") double endLng,     @Param("endLat") double endLat,
                @Param("radius") double radiusMeters,
                @Param("minCov") double minCoverage,
                @Param("limit") int limit
        );

        @Query(value = """
      SELECT ST_Distance(t.route::geography, ST_SetSRID(ST_MakePoint(:lng,:lat),4326)::geography)
      FROM trips t WHERE t.id = :tripId
      """, nativeQuery = true)
        Double distanceToRoute(@Param("tripId") UUID tripId,
                               @Param("lng") double lng,
                               @Param("lat") double lat);
}


