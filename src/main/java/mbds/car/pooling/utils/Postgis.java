package mbds.car.pooling.utils;

import lombok.experimental.UtilityClass;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

@UtilityClass
public class Postgis {
    public static double distancePointToPointMeters(Point a, double lng, double lat) {
        // utilisant JTS + geodesic approximée par Geography : on passe par SQL si tu préfères la vraie géodésie
        org.locationtech.jts.geom.GeometryFactory gf = new org.locationtech.jts.geom.GeometryFactory(new PrecisionModel(), 4326);
        Point b = gf.createPoint(new Coordinate(lng, lat));
        // fallback approximatif en WGS84 : mieux vaut une native query pour une vraie distance en mètres
        // Option recommandée : faire 2 petites natives queries avec ST_Distance(...::geography)
        return a.distance(b) * 111_000; // approx m (correcte pour courte distance)
    }
}
