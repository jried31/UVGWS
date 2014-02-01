 package navigation.shared;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import navigation.server.BoundingBox;
import org.geotools.geometry.jts.JTSFactoryFinder;

/**
 * @author Jerrid
 *
 * A step consists of multiple line segments used to make the road path. These
 * segments are extracted from OpenStreetMaps so that we can obtain information
 * about each segment This class is used to initialize the segments by calling
 * the database to get the UV values of points inside the segment object.
 */
public class Segment {

    private LatLong start_location;
    private LatLong end_location;
    private double averageUvi = 0;
    private int no_of_readings = 0;
    private int numTimesInShadow=0, numTimesInSun=0;
    private double distanceInShadow, distanceInSun; //in km
    public static final double MAX_SEGMENT_SIZE = .005;//in km
    private int numSegmentSlices = 1; //a segment counts as a segment slice itself

    /**
     * @throws SQLException Initialize the segment by getting the readings for
     * each segment
     */
    public void initialize() throws SQLException {
        if (Util.getDistance(getStart_point(), getEnd_point()) > MAX_SEGMENT_SIZE){
            split();
            return;
        }
        getReadings();
    }

    public double getDistanceInShadow() {
        return distanceInShadow;
    }

    public double getDistanceInSun() {
        return distanceInSun;
    }
    
    /*
    *   Splits a segment into 2 smaller (and equal sized) segments, used if the segment is too large
    */
    private void split() throws SQLException {
            Segment s1 = new Segment();
            s1.setStart_point(getStart_point());
            s1.setEnd_point(getMidpoint());
            
            Segment s2 = new Segment();
            s2.setStart_point(getMidpoint());
            s2.setEnd_point(getEnd_point());
            
            s1.initialize();
            s2.initialize();
            
            this.numSegmentSlices = s1.getNumSegmentSlices() + s2.getNumSegmentSlices();
            this.numTimesInShadow = s1.getNumTimesInShadow() + s2.getNumTimesInShadow();
            this.numTimesInSun = s1.getNumTimesInSun() + s2.getNumTimesInSun();
            this.distanceInShadow = s1.getDistanceInShadow() + s2.getDistanceInShadow();
            this.distanceInSun = s1.getDistanceInSun() + s2.getDistanceInSun();
            setUvi((s1.getUvi() + s2.getUvi()) / 2);
    }

    public int getNumTimesInShadow() {
        return this.numTimesInShadow;
    }
    
    public int getNumTimesInSun() {
        return this.numTimesInSun;
    }
    
    @Override
    public String toString() {

        return start_location + "," + end_location + "," + averageUvi + "," + no_of_readings;

    }

    /**
     * @throws SQLException sets the UVA and UVB values for each segment by
     * calling the database and getting the UV values of points in each segment
     * and then taking an average of points
     */
    public void getReadings() throws SQLException {   
        double segmentDistance = Util.getDistance(start_location, end_location);
        if (isInShadow()) {
            distanceInShadow = segmentDistance;
            numTimesInShadow++;
            setUvi(1);
        } else {
            distanceInSun = segmentDistance;
            numTimesInSun++;
            setUvi(4);
        }

    }

    /**
     * @return true if this segment is in the shadow, otherwise false
     */
    public boolean isInShadow() {
        LatLong position = getMidpoint();
        double latitude = position.getLatitude();
        double longitude = position.getLongitude();

        Calendar calendar = Calendar.getInstance();
        
//      final int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
//      final double LT = getTimeInHours();
        final int dayOfYear = 347; //hard coded day of year
        final double LT = 10.25; //hard coded time
        
        final int gmt_adjust = -8; //neither
        final double LSTM = 15 * gmt_adjust; //local solar time meridian, in degrees
        final double B = Math.toRadians((360.0 / 365) * (dayOfYear - 81)); //in radians
        final double EoT = 9.87 * Math.sin(2 * B) - 7.53 * Math.cos(B) - 1.5 * Math.sin(B); // Equation of time, in degrees
        final double TC = 4 * (longitude - LSTM) + EoT; //Time Correction
        final double LST = LT + TC / 60; //LT = local time, LST = local solar time
        final double HRA = Math.toRadians(15 * (LST - 12)); //hour angle, in radians
        final double delta = Math.toRadians(23.45 * Math.sin(B));//in radians
        //elevation angle of the sun
        final double elevAngleSun = Math.asin((Math.sin(delta) * Math.sin(Math.toRadians(latitude)) + Math.cos(delta) * Math.cos(Math.toRadians(latitude)) * Math.cos(HRA)));//in radians

        System.out.println("LT (time): " + LT);
        
        //if the sun is not up
        //find out elevation angle for morning and night (what is the cutoff points)
        if (elevAngleSun <= 0) {
            return true;
        }

        //azimuth angle
        final double theta = Math.acos((Math.sin(delta) * Math.cos(Math.toRadians(latitude)) - Math.cos(delta) * Math.sin(Math.toRadians(latitude)) * Math.cos(HRA)) / Math.cos(elevAngleSun));//in radians
        double azimuthAngle;
        if (LST > 12 || HRA > 0) {
            azimuthAngle = 360 - Math.toDegrees(theta);
        } else {
            azimuthAngle = Math.toDegrees(theta);
        }
        
        try {
            for (Building building : getBuildings(azimuthAngle)) {
                //elevation angle of the building
                double elevAngleBuilding = Math.atan((building.getHeight() - Constants.AVERAGE_PERSON_HEIGHT) / getDistanceToBuilding(new LatLong(latitude, longitude), building));
                //elevation angle of the building/sun from the midpoint of the segment slice
                if (elevAngleBuilding >= elevAngleSun) {
                    return true;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Segment.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /*
     Returns true if it finds a building along the azimuth line. Uses the method of intersecting lines
     of each side of the polygon. The polygon is made up of 4 points. Total number of lines that
     can connect the 4 points is 6. The algorithm checks if the src lat long, give the azimuth
     angle will intersect any 4/6 lines from the polygon. This is more robust than method two.
     The method will return a list of buildings that are in line with the azimuth angle
     */

    public ArrayList<Building> getBuildings(double az) throws Exception {
        Area area = new Area();
        BoundingBox b = new BoundingBox(start_location, end_location,80,120 ); //80 feet buffer between segment end points 
        
        LatLong mid = getMidpoint();
        ArrayList<Building> buildings = area.getBuildingsInBoundingBox(b, mid);
        
        
        // this list of relevant buildings is returned. We are returning a list of
        // buildings because in rare cases if a tall building is behind another building
        // along the azimuth line we want to consider that as well
        ArrayList<Building> relevantBuildings = new ArrayList<Building>();
        // need to convert az here for different quadrants
        double azC = azConversion(az);
        System.out.println("Azimuth:  " + az + " az after conversion: " + azC);
        
        for (Building building : buildings) {
            long id = building.getId();
            
            Coordinate[] corners  = building.getCorners();
            
            GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory( null );
            LinearRing ring = geometryFactory.createLinearRing( corners );
            LinearRing holes[] = null; // use LinearRing[] to represent holes
            Polygon polygon = geometryFactory.createPolygon(ring, holes );
            
            LatLong myLocation = getMidpoint();
            //Grab the sun's location and your location
            
            double DEGREE_VARIANCE_PER_FEET = 0.0000027;
            double endpointBuffer = 100 * DEGREE_VARIANCE_PER_FEET;
        
            //We have segment midpoint and Azimuth angle, so we will make a line segment +- position
            Coordinate[] lineSegment  = new Coordinate[] {
                new Coordinate(azC*(myLocation.getLongitude() + endpointBuffer - myLocation.getLongitude()) + myLocation.getLatitude(), myLocation.getLongitude() + endpointBuffer), 
                new Coordinate(azC*(myLocation.getLongitude() - endpointBuffer - myLocation.getLongitude()) + myLocation.getLatitude(), myLocation.getLongitude() - endpointBuffer)
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

    /*
     Because the azimuth angle assumes that 0 degrees is true north, and regular cartesian degrees uses east as the 0 degree, there is a need
     to convert the azimuth angle when we are in different quadrants.
     this converts az angle into the form that is relevant for slope calculations
     */
    public double azConversion(double az) {
        if (az < 90) {
            az = 90 - az;
        } else if (az > 90 && az < 180) {
            az = az - 90;
            az = -az;
        } else if (az > 180 && az < 270) {
            az = 270 - az;
        } else if (az > 270 && az < 360) {
            az = az - 270;
            az = -az;
        }

        return az;
    }

    /*
     returns true if the line project from the az angle intersects the line variable
     az = azimuth angle, lat, lon is the coordinates of the midpoint of the segment
     line is the line segment that is tested for intersection. line[0] is the slope 
     of the line, line[1] is the y intercept. point1 and point2 are the 2 endpoints
     of line
    Note: az is in degrees
     */
    public boolean intersectLines(double az, double lat, double lon, double[] line,UtmLatLong point1, UtmLatLong point2) {
        double srcB = lon - (Math.tan(Math.toRadians(az)) * lat);
        double x = (line[1] - srcB) / (Math.tan(Math.toRadians(az)) - line[0]);
        double y = line[0] * x + line[1];

        if (x < Math.max(point1.getLatitude(), point2.getLatitude())
                && x > Math.min(point1.getLatitude(), point2.getLatitude())
                && y < Math.max(point1.getLongitude(), point2.getLongitude())
                && y > Math.min(point1.getLongitude(), point2.getLongitude())) {

            return true;
        }
        return false;
    }

    // returns a line equation in slope intercept form from 2 points
    // line[0] holds the slope, line[1] holds the y intercept
    public double[] getLine(double x1, double y1, double x2, double y2) {
        double slope = (y1 - y2) / (x1 - x2);
        double b = y1 - (slope * x1);
        double[] line = new double[2];
        line[0] = slope;
        line[1] = b;
        return line;
    }

    public UtmLatLong[] closestLine(UtmLatLong source, UtmLatLong p1, UtmLatLong p2, UtmLatLong p3, UtmLatLong p4) {
        UtmLatLong[] line = new UtmLatLong[2];
        double min = Util.getDistanceUTM(source, p1);
        double min2 = Util.getDistanceUTM(source, p2);
        UtmLatLong minLL = p1;
        UtmLatLong min2LL = p2;
        if (min2 < min) {
            double temp = min;
            UtmLatLong tempLL = minLL;
            min = min2;
            min2 = temp;
            minLL = min2LL;
            min2LL = tempLL;
        }
        double dis = Util.getDistanceUTM(source, p3);
        if (dis < min2) {
            min2 = dis;
            min2LL = p3;
            if (min2 < min) {
                double temp = min;
                UtmLatLong tempLL = minLL;
                min = min2;
                min2 = temp;
                minLL = min2LL;
                min2LL = tempLL;
            }
        }
        dis = Util.getDistanceUTM(source, p4);
        if (dis < min2) {
            min2 = dis;
            min2LL = p4;
            if (min2 < min) {
                double temp = min;
                UtmLatLong tempLL = minLL;
                min = min2;
                min2 = temp;
                minLL = min2LL;
                min2LL = tempLL;
            }
        }
        line[0] = minLL;
        line[1] = min2LL;
        return line;
    }

    /**
     * @return the time of day (i.e. 4:30 PM = 16.5)
     */
    public double getTimeInHours() {
        GregorianCalendar now = new GregorianCalendar();
        return now.get(Calendar.HOUR_OF_DAY) + ((double) now.get(Calendar.MINUTE)) / 60 + ((double) now.get(Calendar.SECOND)) / 3600;
    }
    
    /**
     * @param point - the point you are standing at
     * @param b - the building
     * @return the distance to the closest corner of the building
     */
    public double getDistanceToBuilding(LatLong point, Building b) {
        double min = Double.POSITIVE_INFINITY;
        for (Coordinate corner : b.getCorners()) {
            double distance = Util.getDistance(point, new LatLong(corner.y,corner.x));
            if (distance < min) {
                min = distance;
            }
        }
        return min*1000.;
    }

    /**
     * @return the midpoint of this segment
     */
    public LatLong getMidpoint() {
        LatLong startPoint = getStart_point();
        LatLong endPoint = getEnd_point();
        double middleLat = (startPoint.getLatitude() + endPoint.getLatitude()) / 2;
        double middleLong = (startPoint.getLongitude() + endPoint.getLongitude()) / 2;
        return new LatLong(middleLat, middleLong);
    }

    public LatLong getStart_point() {
        return start_location;
    }

    public void setStart_point(LatLong start_point) {
        this.start_location = start_point;
    }

    public LatLong getEnd_point() {
        return end_location;
    }

    public void setEnd_point(LatLong end_point) {
        this.end_location = end_point;
    }

    public double getUvi() {
        return averageUvi;
    }

    public void setUvi(double uvi) {
        this.averageUvi = uvi;
    }
    
    public int getNo_of_readings() {
        return no_of_readings;
    }

    public void setNo_of_readings(int no_of_readings) {
        this.no_of_readings = no_of_readings;
    }
    
    public int getNumSegmentSlices() {
        return numSegmentSlices;
    }

}
