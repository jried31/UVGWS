/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package navigation.shared;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Calendar;

import navigation.server.FastRTParser;
import navigation.shared.LatLong;
import navigation.shared.Person;

/**
 *
 * @author Angela
 */
public class VitaminD {
    
    private String vitaminDTime;
    private String sunburnTime;
    
    public VitaminD() {
        vitaminDTime = "";
        sunburnTime = "";
    }
    
    public String[] getVitaminDandSunburnTime(LatLong start_location, Person person, Calendar calendar) throws URISyntaxException, IOException {
        
        
        
        int month = calendar.MONTH;
        int day = calendar.DAY_OF_MONTH;
        int hour = calendar.HOUR_OF_DAY;
        
        FastRTParser parser = new FastRTParser();
        
        String times[] = parser.parse(start_location, person, month, day, hour);
        
        return times;
    }
    
}
