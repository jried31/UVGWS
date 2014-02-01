/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navigation.shared;

import java.io.File;
import org.geotools.data.FeatureSource;
import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.opengis.feature.type.FeatureType;
import org.opengis.geometry.BoundingBox;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Eric
 * This is a utility class that provides generic functions specific for
 * measurements with geographic coordinates.
 */
public class Util {

    // Generic conversion method for different coordinates systems.
    // This function is used to convert between UTM (universal tranverse
    // meractor) and cartesian coordinates.
    public static double[] convertCRS(CoordinateReferenceSystem sourceCrs, CoordinateReferenceSystem targetCrs, double x, double y) throws NoSuchAuthorityCodeException, FactoryException, MismatchedDimensionException, TransformException {
        boolean lenient = true; // relax accuracy
        MathTransform mathTransform = CRS.findMathTransform(sourceCrs, targetCrs, lenient);

        DirectPosition2D srcDirectPosition2D = new DirectPosition2D(sourceCrs, x, y);
        DirectPosition2D destDirectPosition2D = new DirectPosition2D();
        mathTransform.transform(srcDirectPosition2D, destDirectPosition2D);

        double[] coordinates = {destDirectPosition2D.x, destDirectPosition2D.y};

        return coordinates;
    }

   
    public static DirectPosition2D convertCRS2(CoordinateReferenceSystem sourceCrs, CoordinateReferenceSystem targetCrs, double x, double y) throws NoSuchAuthorityCodeException, FactoryException, MismatchedDimensionException, TransformException {
        boolean lenient = true; // relax accuracy
        MathTransform mathTransform = CRS.findMathTransform(sourceCrs, targetCrs, lenient);

        DirectPosition2D srcDirectPosition2D = new DirectPosition2D(sourceCrs, x, y);
        DirectPosition2D destDirectPosition2D = new DirectPosition2D();
        mathTransform.transform(srcDirectPosition2D, destDirectPosition2D);

        return destDirectPosition2D;
    }
        
    public static double toMeters(double feet) {
        return feet * .3048;
    }
    
    // Uses the Haversine formula to calculate the distance between two LatLong coordinates.
    // http://www.movable-type.co.uk/scripts/latlong.html
    public static double getDistance(LatLong source, LatLong target) {
        int rKm = 6371; //km
        double rM = 3958.75;//miles
        double dLat = Math.toRadians(target.getLatitude() - source.getLatitude());
        double dLon = Math.toRadians(target.getLongitude()- source.getLongitude());
        double a = Math.pow(Math.sin(dLat/2), 2) + 
                Math.cos(Math.toRadians(target.getLatitude())) * 
                Math.cos(Math.toRadians(source.getLatitude())) * 
                Math.pow(Math.sin(dLon/2), 2);
        double c = 2 * Math.atan2(Math.sqrt(a),Math.sqrt(1 - a));
        
        return rKm*c;
    }
    

    public static double getDistanceUTM(UtmLatLong src, UtmLatLong dst) {
        double x1 = src.getLatitude();
        double y1 = src.getLongitude();
        double x2 = dst.getLatitude();
        double y2 = dst.getLongitude();

        double d = Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
        return d;
    }
    
    /**
     * 
     * @param averageUVI - the average UV index of the route
     * @param routeDistance - the distance of the route in meters per second
     * @return e, the amount of energy (Joules) per m ^ 2
     */
    public static double calculateE(double averageUVI, double routeDistance) {
        return .001 * averageUVI * 25 * routeDistance / Constants.AVERAGE_WALKING_SPEED;
    }
    
    public static double getError(double actual, double result) {
        return Math.abs(actual - result) / actual;
    }
}
