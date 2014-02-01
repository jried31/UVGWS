/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package navigation.shared;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author Samson
 */
public class Shadow {
    public double AVERAGE_PERSON_HEIGHT = 5.10 * .3048; //5' 10'' is the average male height, converted to meters
    
    public void something() {
        /*
        for each segment in the path {
        get the buildings near the segment
        get the latitude and longitude of the segment
        determine if the person is in the building's shadow
        determine how much UV exposure the person got in that particular segment (assume a certain walk rate)
        }
        */
    }
    
    public double getDistanceFromBuilding() {
        return 0;
    }
    
    public double getTimeInHours() {
        GregorianCalendar now = new GregorianCalendar();
        return now.get(Calendar.HOUR_OF_DAY) + ((double) now.get(Calendar.MINUTE))/60 + ((double) now.get(Calendar.SECOND))/3600;
    }
    
    /**
     * d - # of days since the start of the year
     * @param latitude - the latitude of the person
     * @param longitude - the longitude of the person
     * @return - true if the person is completely in the building's shadow, otherwise returns false
     */
    public boolean isInShadow(double latitude, double longitude) {        
        Calendar calendar = Calendar.getInstance();
        final int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        final double LT = getTimeInHours();
        final int gmt_adjust = -8;
        final int LSTM = 15  * gmt_adjust; //local solar time meridian
        final double B = 360.0 / 365 * (dayOfYear - 81); //?
        final double EoT = 9.87 * Math.sin(2 * B) - 7.53 * Math.cos(B) - 1.5 * Math.sin(B); // Equation of time
        final double TC = 4 * (longitude - LSTM) + EoT;
        final double LST = LT + TC/60; //(LT = local time?), LST = local solar time?
        final double HRA = 15 * (LST - 12); //hour angle
        
        final double delta = 23.45 * Math.sin(B);
        //elevation angle
        final double alpha = Math.asin((Math.sin(delta) * Math.sin(latitude) + Math.cos(delta) * Math.cos(latitude) * Math.cos(HRA)));
        
        //azimuth angle
        final double theta = Math.acos((Math.sin(delta) * Math.cos(latitude) - Math.cos(delta) * Math.sin(latitude) * Math.cos(HRA))/Math.cos(alpha));
        double AZ;
        if (LST > 12 || HRA > 0) {
            AZ = 360 - theta;
        } else {
            AZ = theta;
        }
        for (Building building : getBuildings(AZ)) {
            double beta = Math.atan((building.getHeight() - AVERAGE_PERSON_HEIGHT) / getDistanceFromSidewalk());
            if (beta >= alpha) {
                return true;
            }
        }
        return false;
    }
    
    public Building[] getBuildings(double azimuthAngle) {
        return null;//TODO, change
    }
    
    public double getDistanceFromSidewalk() {
        return 0; //TODO, change
    }  
    
    //put this into Util.java later
    //returns the distance in km, input LatLong in degrees
    public double getDistance(UtmLatLong one, UtmLatLong two) {
        final int R = 6371; // km
        double dLat = Math.toRadians(two.getLatitude() - one.getLatitude());
        double dLon = Math.toRadians(two.getLatitude() - one.getLongitude());
        double lat1 = Math.toRadians(one.getLatitude());
        double lat2 = Math.toRadians(two.getLatitude());

        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.sin(dLon/2) * Math.sin(dLon/2) * Math.cos(lat1) * Math.cos(lat2); 
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
        double d = R * c;
        return d;
    }
}
