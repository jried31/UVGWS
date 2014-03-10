/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navigation.util;

import parse.almonds.Parse;

/**
 *
 * @author user510
 */
public class Constants {

    public final static String PATH = "resources/";

    public final static String SHP = "lariac_buildings_2008_4326.shp";
    public final static String UV_IRRADIANCE = "Data635109626095766954.csv";
    public final static String CIE = "CIE.txt";
    public final static String CURRENT_UVI = "current_uvi.csv";

    public final static int LATITUDE = 0;
    public final static int LONGITUDE = 1;

    public final static boolean REAL_TIME = false;

    public final static double AVERAGE_PERSON_HEIGHT = 1.77; //1.77 meters is the average male height
    public final static double AVERAGE_WALKING_SPEED = 1.3; //meters per second
    
    //data taken from European Organisation for Research and Treatment of Cancer
    public final static double AVERAGE_HUMAN_SURFACE_AREA = 1.73; // square meters
    
    //value is only an estimate, no study found at this time
    public final static double DEFAULT_EXPOSED_SKIN_PERCENT = 0.2;
    
    //Amount of UV exposure blocked by shadows, find more concrete answers
    public final static double SHADOW_DAMPING_FACTOR = 0.5;
    public final static double SHADE_UVI = 1.5;
    
    public final static double DEGREE_VARIANCE_PER_FEET = 0.0000027;

    public static long UVI_UPDATE_INTERVAL= 1800000;
    
    public final static double THIRTY_MINUTES_IN_MILLIS = 1800000;
}
