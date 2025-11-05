package mbds.car.pooling.services;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
import org.springframework.stereotype.Service;

@Service
public interface GeometryFactoryService {

    Point point(double lng, double lat);

    LineString line(double[][] lonLatPairs);
}
