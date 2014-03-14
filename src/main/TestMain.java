/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package main;

import java.io.IOException;
import java.net.URISyntaxException;
import navigation.server.FastRTParser;
import navigation.shared.*;

/**
 *
 * @author Angela
 */
public class TestMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws URISyntaxException, IOException {
        LatLong start_location = new LatLong(50.5, 4.2);
        
        Person person = new Person("SKIN-4","0");
        Person person2 = new Person("SKIN-3","0",50); //(int skinType, int SPF, double bodyExposure)
        
        System.out.println("Person 1 Skin type: " + person.getSkinType());
        System.out.println("Person 1 SPF: " + person.getSPF());
        System.out.println("Person 2 Skin Type: " + person2.getSkinType());

        person.clothesToBodyExposure("NONE", "SLEEVES-TANK", "PANTS-SHORTS"); //(hat, upper, lower)
        
        person.setBodyExposure(60);


        VitaminD vitD = new VitaminD();

        // (LatLong, Person, String dateTime)
        String[] times = vitD.getVitaminDandSunburnTime(start_location, person, "2014-03-19T13:00"); 
        
        System.out.println("Vitamin D Time: " + times[0]);
        System.out.println("Sunburn Time: " + times[1]);
    }
    
}
