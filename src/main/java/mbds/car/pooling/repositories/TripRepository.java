package mbds.car.pooling.repositories;

import mbds.car.pooling.entities.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, UUID> {

    @Query(value = """
  WITH params AS (
    SELECT
      ST_SetSRID(ST_MakePoint(:startLng, :startLat), 4326) AS u_start_geom,
      ST_SetSRID(ST_MakePoint(:endLng,   :endLat),   4326) AS u_end_geom
  ),
  cand AS (
    SELECT
      t.*,
      ST_LineLocatePoint(t.route, p.u_start_geom) AS t_start,
      ST_LineLocatePoint(t.route, p.u_end_geom)   AS t_end
    FROM trips t
    CROSS JOIN params p
    WHERE
      ST_DWithin(t.route::geography, p.u_start_geom::geography, :radius)
      AND ST_DWithin(t.route::geography, p.u_end_geom::geography, :radius)
      AND ST_LineLocatePoint(t.route, p.u_end_geom) > ST_LineLocatePoint(t.route, p.u_start_geom)
      AND (ST_LineLocatePoint(t.route, p.u_end_geom) - ST_LineLocatePoint(t.route, p.u_start_geom)) >= :minCoverage
  )
  SELECT * FROM cand
  ORDER BY ST_Distance(route::geography, (SELECT u_start_geom FROM params)::geography)
  LIMIT :limit
  """, nativeQuery = true)
    List<Trip> searchMatching(
            @Param("startLng") double startLng, @Param("startLat") double startLat,
            @Param("endLng") double endLng, @Param("endLat") double endLat,
            @Param("radius") double radiusMeters,
            @Param("minCoverage") double minCoverage,
            @Param("limit") int limit
    );
}
