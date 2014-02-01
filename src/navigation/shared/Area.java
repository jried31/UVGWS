package navigation.shared;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import navigation.server.BoundingBox;

import java.io.File;
import java.io.IOException;

import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.FeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.geotools.referencing.CRS;
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
import org.geotools.geometry.jts.JTSFactoryFinder;

/**
 * @author Eric It is necessary to have both .shp and .dbf files within the
 * working directory.
 */
public class Area {

    // Creates a custom bounding box and returns all buildings within it.
    // Input bb coordinates must be converted into UTM format prior to proccessing.
    // Processes BoundingBox as param to create new a bounding box.
    public ArrayList<Building> getBuildingsInBoundingBox(BoundingBox bb, LatLong myLocation) throws IOException, MismatchedDimensionException, NoSuchAuthorityCodeException, FactoryException, TransformException {
        ArrayList<Building> list = new ArrayList<Building>();

        File file = new File(Constants.PATH + Constants.SHP);
        FileDataStore store = FileDataStoreFinder.getDataStore(file);
        FeatureSource source = store.getFeatureSource();

        FilterFactory2 ff = CommonFactoryFinder.getFilterFactory2();
        FeatureType schema = source.getSchema();
        String geometryPropertyName = schema.getGeometryDescriptor().getLocalName();
        
        // creates custom bounding box using input bounding box coordinates
        Polygon polygon = bb.getBoundingBoxAsPolygon();

        // filters buildings within the bounding box of the start and end points
        Filter filter = ff.intersects(ff.property(geometryPropertyName), ff.literal(polygon));
        FeatureCollection collection = source.getFeatures(filter);
        System.out.println("Collection size: " + collection.size());
        FeatureIterator iterator = collection.features();

        Building b;
        try {
            while (iterator.hasNext()) {
                Feature feature = iterator.next();
                System.out.println(feature);
                
                b = new Building(feature);
                list.add(b);
                System.out.println(feature.getIdentifier());
                System.out.println(feature.getDefaultGeometryProperty().getValue());
                System.out.println("Building ID: " + feature.getProperty("BLD_ID").getValue());
                System.out.println("Height: " + feature.getProperty("HEIGHT").getValue());
                System.out.println(feature.getDefaultGeometryProperty().getBounds());
                System.out.println("Center of building: " + b.getCenterPoint());
            }
        } finally {
            iterator.close();
        }

        return sortByDistance(list, myLocation);
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
