 package navigation.shared;

import navigation.util.Constants;
import navigation.util.Util;
import navigation.util.BuildingUtils;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Polygon;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import navigation.util.SunUtil;
import java.lang.IllegalArgumentException;
/**
 * @author Jerrid
 *
 * A step consists of multiple line segments used to make the road path. These
 * segments are extracted from OpenStreetMaps so that we can obtain information
 * about each segment This class is used to initialize the segments by calling
 * the database to get the UV values of points inside the segment object.
 */
public class Segment {

    private Calendar start_time;
    private Calendar end_time;
    private LatLong start_location;
    private LatLong end_location;
    private Calendar start_time;
    private Calendar end_time;
    private double averageUvi = 0;
    private double measuredUVI = 0;
    private double effectiveUVI = 0;
    private int no_of_readings = 0;
    private int numTimesInShadow=0, numTimesInSun=0;
    private int durationInShadow, durationInSun; // in sec
    private double distanceInShadow, distanceInSun; //in km
    public static final double MAX_SEGMENT_SIZE = .005;//in km
    private int numSegmentSlices = 1; //a segment counts as a segment slice itself

    /**
     * @throws SQLException Initialize the segment by getting the readings for
     * each segment
     */
    public void initialize() throws SQLException,Exception {
        double distance=Util.getDistance(getStart_point(), getEnd_point()) ;
        if (distance > MAX_SEGMENT_SIZE){
            split();
            return;
        }
        else {
            getReadings();
        }
    }
    
    public void initialize(LatLong startPoint, LatLong endPoint, Calendar startTime, Calendar endTime) throws Exception {
        setStart_point(startPoint);
        setEnd_point(endPoint);
        setStart_time(startTime);
        setEnd_time(endTime);
        initialize();
    }
    
    public int getDurationInShadow() {
        return durationInShadow;
    }
    
    public int getDurationInSun() {
        return durationInSun;
    }
    
    //to get rid of errors, remove if unnecessary in final version
    public Segment()
    {
        return;
    }
    
    public Segment(LatLong start_coords,LatLong end_coords,Calendar start_t,Calendar end_t, double UVI) throws Exception
    {
        if(Util.getDistance(start_coords, end_coords) > this.MAX_SEGMENT_SIZE)
            throw new IllegalArgumentException();
        this.start_location = start_coords;
        this.end_location = end_coords;
        this.start_time = start_t;
        this.end_time = end_t;
        this.measuredUVI = UVI;
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
    private void split() throws SQLException,Exception {
            LatLong midPoint = getMidpoint();
            int secondsDifference = timeDifferenceInSeconds(getStart_time(), getEnd_time());
            Calendar midTime = (Calendar) getStart_time().clone();
            midTime.add(Calendar.SECOND, secondsDifference/2);
            
            Segment s1 = new Segment();
            s1.setStart_point(getStart_point());
            s1.setEnd_point(midPoint);
            s1.setStart_time(getStart_time());
            s1.setEnd_time(midTime);
            
            Segment s2 = new Segment();
            s2.setStart_point(midPoint);
            s2.setEnd_point(getEnd_point());
            s2.setStart_time(midTime);
            s2.setEnd_time(getEnd_time());
            
            s1.initialize();
            s2.initialize();
            
            this.no_of_readings = s1.getNo_of_readings() + s2.getNo_of_readings();
            this.numTimesInShadow = s1.getNumTimesInShadow() + s2.getNumTimesInShadow();
            this.numTimesInSun = s1.getNumTimesInSun() + s2.getNumTimesInSun();
            this.distanceInShadow = s1.getDistanceInShadow() + s2.getDistanceInShadow();
            this.distanceInSun = s1.getDistanceInSun() + s2.getDistanceInSun();
            this.durationInShadow = s1.getDurationInShadow() + s2.getDurationInShadow();
            this.durationInSun = s1.getDurationInSun() + s2.getDurationInSun();
            setUvi((s1.getUvi() + s2.getUvi()) / 2);
    }
    
    private int timeDifferenceInSeconds(Calendar earlier, Calendar later)
    {
        long dmiliseconds = later.getTimeInMillis() - earlier.getTimeInMillis();
        int dseconds = (int) (dmiliseconds / 1000);
        return dseconds;
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
    public void getReadings() throws SQLException,Exception {   
        double segmentDistance = Util.getDistance(start_location, end_location);
        int segmentDuration = timeDifferenceInSeconds(getStart_time(), getEnd_time());
        no_of_readings = 1;
        if (isInShadow()) {
            System.out.println("SEGMENT IN SHADOW");
            distanceInShadow = segmentDistance;
            durationInShadow = segmentDuration;
            numTimesInShadow++;
            setUvi(1);
        } else {
            System.out.println("SEGMENT IN SUN");
            distanceInSun = segmentDistance;
            durationInSun = segmentDuration;
            numTimesInSun++;
            setUvi(4);
        }
    }

    /**
     * @return true if this segment is in the shadow, otherwise false
     */
    public boolean isInShadow() throws Exception {
        //Get the Buildings within segment
        BoundingBox bb = new BoundingBox(start_location, end_location,80,120 ); //80 feet buffer between segment end points 
        BuildingUtils bbutil = new BuildingUtils();
        bbutil.setEndpointBuffer(528);//528feet is the length of this Azmuth line
        LatLong midSegmentPosition = getMidpoint();
        SunUtil sunUtil = new SunUtil(midSegmentPosition);
        
        //Compute Sun Angles
        sunUtil.computeSunAngles(start_time, midSegmentPosition);
        
         // creates custom bounding box using input bounding box coordinates
        Polygon polygon = bb.getBoundingBoxAsPolygon();
        ArrayList<Building> list = bbutil.getBuildingsInBoundingBox(polygon);
        list = bbutil.sortByDistance(list, midSegmentPosition);
        /*
     Returns true if it finds a building along the azimuth line. Uses the method of intersecting lines
     of each side of the polygon. The polygon is made up of 4 points. Total number of lines that
     can connect the 4 points is 6. The algorithm checks if the src lat long, give the azimuth
     angle will intersect any 4/6 lines from the polygon. This is more robust than method two.
     The method will return a list of buildings that are in line with the azimuth angle
     */

        //Grab the relevant buildings (which are all buildings that intersect the Sun and the persons position
        list = bbutil.getBuildingsBlockingSun(list, sunUtil,midSegmentPosition);
        
        try {
            for (Building building : list) {
                //Compute the elevation angle between the building and the person angle of the building
                double elevAngleBuilding = Math.atan((building.getHeight() - Constants.AVERAGE_PERSON_HEIGHT) / getDistanceToBuilding(midSegmentPosition, building));
                //elevation angle of the building/sun from the midpoint of the segment slice
                System.out.println("Elevation of building: " + elevAngleBuilding);
                System.out.println("Elevation of sun: " + sunUtil.getElevationAngle());
                if (elevAngleBuilding >= sunUtil.getElevationAngle()) {
                    return true;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(Segment.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
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
    
    public Calendar getStart_time() {
        return start_time;
    }
    
    public void setStart_time(Calendar time) {
        start_time = (Calendar) time.clone();
    }
    
    public Calendar getEnd_time() {
        return end_time;
    }
    
    public void setEnd_time(Calendar time) {
        end_time = (Calendar) time.clone();
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

    public void setStartTime(Calendar time) {
        this.start_time = time;
    }

    public Calendar getStartTime() {
        return start_time;
    }

    public void setEndTime(Calendar time) {
        this.end_time = time;
    }

    public Calendar getEndTime() {
        return end_time;
    }
    
    //returns duration along segment
    public double getSegmentDuration() {
        return (end_time.getTimeInMillis() - start_time.getTimeInMillis()) * 1000;
    }
    
    
    public void calculateEffectiveUVI() 
    {
        double eUVI = measuredUVI;
        try {
            if (this.isInShadow()) {
                //eUVI = eUVI * Constants.SHADOW_DAMPING_FACTOR;
                eUVI = 1.5; //place as constant
            }
        }
        catch (Exception e) {
            this.effectiveUVI = 0;
            e.printStackTrace();   
        }
        this.effectiveUVI = eUVI;
    }
    
    // returns the total UV exposure (in Joules) for this segment
    public double getEffectiveUVDose() {
        Util utils = new Util();
        return utils.computeExposure(this.effectiveUVI, this.getSegmentDuration());
    }
}
