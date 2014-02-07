/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package navigation.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
import navigation.shared.LatLong;

/**
 *
 * @author jried31
 */
public class SunUtil {
    private double elevAngleSun,azimuthAngle;
    public double getElevationAngle(){return elevAngleSun;}
    public double getAzmuthAngle(){return azimuthAngle;}
    
    public void computeSunAngles(Calendar calendar,LatLong position){
        
        double latitude = position.getLatitude();
        double longitude = position.getLongitude();
        
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        double LT = getTimeInHours();
        //final int dayOfYear = 347; //hard coded day of year
        //final double LT = 10.25; //hard coded time
        
        final int gmt_adjust = -8; //neither
        final double LSTM = 15 * gmt_adjust; //local solar time meridian, in degrees
        final double B = Math.toRadians((360.0 / 365) * (dayOfYear - 81)); //in radians
        final double EoT = 9.87 * Math.sin(2 * B) - 7.53 * Math.cos(B) - 1.5 * Math.sin(B); // Equation of time, in degrees
        final double TC = 4 * (longitude - LSTM) + EoT; //Time Correction
        final double LST = LT + TC / 60; //LT = local time, LST = local solar time
        final double HRA = Math.toRadians(15 * (LST - 12)); //hour angle, in radians
        final double delta = Math.toRadians(23.45 * Math.sin(B));//in radians
        //elevation angle of the sun
        elevAngleSun = Math.asin((Math.sin(delta) * Math.sin(Math.toRadians(latitude)) + Math.cos(delta) * Math.cos(Math.toRadians(latitude)) * Math.cos(HRA)));//in radians

        System.out.println("LT (time): " + LT);
        
        //if the sun is not up
        //find out elevation angle for morning and night (what is the cutoff points)
        if (elevAngleSun <= 0) {
            return;
        }

        //azimuth angle
        final double theta = Math.acos((Math.sin(delta) * Math.cos(Math.toRadians(latitude)) - Math.cos(delta) * Math.sin(Math.toRadians(latitude)) * Math.cos(HRA)) / Math.cos(elevAngleSun));//in radians
        if (LST > 12 || HRA > 0) {
            azimuthAngle = 360 - Math.toDegrees(theta);
        } else {
            azimuthAngle = Math.toDegrees(theta);
        }
        
        // need to convert az here for different quadrants
        System.out.println("Azimuth:  " + azimuthAngle + " az after conversion: " + azConversion(azimuthAngle));
        azimuthAngle = azConversion(azimuthAngle);
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

    
        /**
     * @return the time of day (i.e. 4:30 PM = 16.5)
     */
    public double getTimeInHours() {
        GregorianCalendar now = new GregorianCalendar();
        return now.get(Calendar.HOUR_OF_DAY) + ((double) now.get(Calendar.MINUTE)) / 60 + ((double) now.get(Calendar.SECOND)) / 3600;
    }
}
