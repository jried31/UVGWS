/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package navigation.shared;

import java.util.Date;

/**
 *
 * @author Sam
 */
public class Path {
    LatLong start_location;
    LatLong end_location;
    Date start_time;
    Date end_time;
    
    /**
     * Subclasses integrate UV exposure.
     * 
     * @param person skin type, etc.
     * @return UV exposure in Joules
     */
    public double uv(Person person) {
        return 0.0;
    }
    
    /**
     * Subclasses integrate vitamin D production.
     * 
     * @param person skin type, etc.
     * @return "vitamin D minutes"
     */
    public double vitaminD(Person person) {
        return 0.0;
    }
}
  