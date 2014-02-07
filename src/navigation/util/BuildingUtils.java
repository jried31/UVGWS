package navigation.util;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

import java.io.File;
import java.io.IOException;

import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.opengis.feature.Feature;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import navigation.shared.Building;
import navigation.shared.LatLong;
import org.geotools.geometry.jts.JTSFactoryFinder;

/**
 * @author Eric It is necessary to have both .shp and .dbf files within the
 * working directory.
 */
public class BuildingUtils {
	private final double DEGREE_VARIANCE_PER_FEET = 0.0000027;
	private double endpointBuffer = 100;
        

    public BuildingUtils( ){
        
    }
    // Creates a custom bounding box and returns all buildings within it.
    // Input bb coordinates must be converted into UTM format prior to proccessing.
    // Processes BoundingBox as param to create new a bounding box.
    public ArrayList<Building> getBuildingsInBoundingBox( Polygon boundingBox) throws IOException, MismatchedDimensionException, NoSuchAuthorityCodeException, FactoryException, TransformException {
        
        ArrayList<Building> list = new ArrayList<Building>();

        File file = new File(Constants.PATH + Constants.SHP);
        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        FeatureSource source = store.getFeatureSource();
        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
        FeatureType schema = source.getSchema();
        String geometryPropertyName = schema.getGeometryDescriptor().getLocalName();
        CoordinateReferenceSystem sourceCrs = schema.getGeometryDescriptor().getCoordinateReferenceSystem();

        // filters buildings within the bounding box of the start and end points
        Filter filter = ff.intersects(ff.property(geometryPropertyName), ff.literal(boundingBox));
        FeatureCollection collection = source.getFeatures(filter);
        System.out.println("Collection size: " + collection.size());
        FeatureIterator iterator = collection.features();

        Building b;
        try {
            while (iterator.hasNext()) {
                Feature feature = iterator.next();
                b = new Building(feature);
                list.add(b);
            }
        } finally {
            iterator.close();
        }

        return list;//sortByDistance(list, myLocation);
    }

    public ArrayList<Building> getBuildingsBlockingSun(ArrayList<Building> buildings,SunUtil sunUtil,LatLong myLocation){
        // this list of relevant buildings is returned. We are returning a list of
        // buildings because in rare cases if a tall building is behind another building
        // along the azimuth line we want to consider that as well
        ArrayList<Building> relevantBuildings = new ArrayList<Building>();
        
        double azmuthAngle = sunUtil.getAzmuthAngle();
        
        for (Building building : buildings) {
            long id = building.getId();
            
            Coordinate[] corners  = building.getCorners();
            
            GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
            LinearRing ring = geometryFactory.createLinearRing( corners );
            LinearRing holes[] = null; // use LinearRing[] to represent holes
            Polygon polygon = geometryFactory.createPolygon(ring, holes );
            
        
            //We have segment midpoint and Azimuth angle, so we will make a line segment +- position
            Coordinate[] lineSegment  = new Coordinate[] {
                new Coordinate(azmuthAngle*(myLocation.getLongitude() + endpointBuffer - myLocation.getLongitude()) + myLocation.getLatitude(), myLocation.getLongitude() + endpointBuffer), 
                new Coordinate(azmuthAngle*(myLocation.getLongitude() - endpointBuffer - myLocation.getLongitude()) + myLocation.getLatitude(), myLocation.getLongitude() - endpointBuffer)
            };
            
            for(int i = 0;i < 2;i++){
                System.out.println("Point: "+lineSegment[i].x + ", " + lineSegment[i].y);
            }
            
            LineString line = geometryFactory.createLineString(lineSegment);

            if(line.intersects(polygon)){
                System.out.println("FOUND ID: " + id);
                relevantBuildings.add(building);
            }
        }
        return relevantBuildings;
    }
    
    // Buildings are sorted based on distance to the source coordinate from closest to farthest.
    public ArrayList<Building> sortByDistance(ArrayList<Building> buildings, final LatLong myLocation) {
        Collections.sort(buildings, new Comparator<Building>() {
            @Override
            public int compare(Building a, Building b) {
                int value = 0;
                if (Util.getDistance(a.getCenterPoint(), myLocation) < Util.getDistance(b.getCenterPoint(), myLocation)) {
                    value = -1;
                }else
                if (Util.getDistance(a.getCenterPoint(), myLocation) > Util.getDistance(b.getCenterPoint(), myLocation)) {
                    value = 1;
                }
                return value;
            }
        });
        return buildings;
    }
}
