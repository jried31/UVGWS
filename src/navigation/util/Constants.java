/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package navigation.util;

/**
 *
 * @author user510
 */
public class Constants {

    public final static String PATH = "resources/";

    public final static String SHP = "lariac_buildings_2008_4326.shp";
    public final static String UV_IRRADIANCE = "Data635109626095766954.csv";
    public final static String CIE = "CIE.txt";

    public final static int LATITUDE = 0;
    public final static int LONGITUDE = 1;

    public final static boolean REAL_TIME = false;

    public final static double AVERAGE_PERSON_HEIGHT = 1.77; //1.77 meters is the average male height
    public final static double AVERAGE_WALKING_SPEED = 1.3;//meters per second
    
    //data taken from European Organisation for Research and Treatment of Cancer
    public final static double AVERAGE_HUMAN_SURFACE_AREA = 1.73 // square meters
    
    //value is only an estimate, no study found at this time
    public final static double AVERAGE_EXPOSED_SKIN_PERCENT = 0.2
}
