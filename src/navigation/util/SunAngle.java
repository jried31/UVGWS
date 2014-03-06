/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package navigation.util;

/**
 *
 * @author jried31
 */
public class SunAngle {
    private String time;
    private float altitude;
    private float azimuth;

    public String getTime(){return time;}
    public float getAltitude(){return altitude;}
    public float getAzimuth(){return azimuth;}
    
    SunAngle(String time, float altitude, float azimuth) {
        this.time=time;
        this.altitude=altitude;
        this.azimuth=azimuth;
    }
    
}
