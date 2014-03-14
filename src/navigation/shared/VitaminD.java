/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package navigation.shared;

import java.io.IOException;
import java.lang.String;
import java.lang.Integer;
import java.lang.Double;
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
    
    public String getVitaminDTime() {
        return vitaminDTime;
    }
    
    public String getSunburnTime() {
        return sunburnTime;
    }
    
    public String[] getVitaminDandSunburnTime(LatLong start_location, Person person, String dateTime) throws URISyntaxException, IOException {
        
        String month_text = dateTime.substring(5,7);
        String day_text = dateTime.substring(8,10);
        String hour_text = dateTime.substring(11, 13);
        String minute_text = dateTime.substring(14);
        
        //creates a double value out of hour and minute values
        double hour = Double.parseDouble(hour_text);
        double minute = Double.parseDouble(minute_text);
        
        minute = minute/60;
        
        hour += minute;
        
        //gets int from month and day
        int month = Integer.parseInt(month_text);
        int day = Integer.parseInt(day_text);
        
        FastRTParser parser = new FastRTParser();
        
        String times[] = parser.parse(start_location, person, month, day, hour);
        
        vitaminDTime = times[0];
        sunburnTime = times[1];
        
        return times;
    }
    
}
