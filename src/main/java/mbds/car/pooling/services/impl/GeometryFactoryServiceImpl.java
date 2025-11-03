package mbds.car.pooling.services.impl;

import mbds.car.pooling.services.GeometryFactoryService;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.springframework.stereotype.Service;

@Service
public class GeometryFactoryServiceImpl implements GeometryFactoryService {
    private final GeometryFactory gf = new GeometryFactory(new PrecisionModel(), 4326);

    public Point point(double lng, double lat) {
        return gf.createPoint(new Coordinate(lng, lat));
    }

    public LineString line(double[][] lonLatPairs) {
        Coordinate[] coords = new Coordinate[lonLatPairs.length];
        for (int i = 0; i < lonLatPairs.length; i++) {
            coords[i] = new Coordinate(lonLatPairs[i][0], lonLatPairs[i][1]); // [lng,lat]
        }
        return gf.createLineString(new CoordinateArraySequence(coords));
    }
}

