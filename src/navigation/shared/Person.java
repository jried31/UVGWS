/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package navigation.shared;

/**
 *
 * @author tsepuiwah
 */
public class Person {
    //body exposure array positions
    private static final int BASEBALL_CAP = 0;
    private static final int SUNGLASS = 1;
    
    private static final int SPORTS_BRA = 2;
    private static final int TANK = 3;
    private static final int SHORT_SLEEVES = 4;
    private static final int LONG_SLEEVES = 5;
    
    private static final int SHORTS = 6;
    private static final int MEDIUM_PANTS = 7;
    private static final int LONG_PANTS = 8;
    private static final int NONE = 9;
    
    private double[] bodyCoverage = {1.5, 1, 13, 26, 30, 40, 18, 25, 32, 0};
    
    private int skinType;
    private int SPF;
    private double bodyExposure; //as a percentage, 0-100, 0 = fully covered, 100 = no coverage
    
    public Person(int skinType, int SPF, int bodyExposure) {
        this.skinType = skinType;
        this.SPF = SPF;
        this.bodyExposure = bodyExposure;
    }
    
    // give default value for bodyExposure if not calculated yet
    public Person(int skinType, int SPF) {
        this.skinType = skinType;
        this.SPF = SPF;
        this.bodyExposure = 25;
    }
    
    public int getSkinType() {
        return this.skinType;
    }

    public int getSPF() {
        return this.SPF;
    }
    
    public double getBodyExposure() {
        return this.bodyExposure;
    }

    public void changeSkinType(int skinType) {
        this.skinType = skinType;
    }

    public void changeSPF(int SPF) {
        this.SPF = SPF;
    }
    
    public void changeBodyExposure(double bodyExposure) {
        this.bodyExposure = bodyExposure;
    }
    
    public void clothesToBodyExposure(String head, String upper, String lower) {
        double total = 0;
        //head
        if (head.equals("BASEBALL_CAP")) {
            total += 1.5;
        } else if (head.equals("SUNGLASS")) {
            total += 1;
        }
        //upper
        if (upper.equals("SPORTS_BRA")) {
            total += 13;
        } else if (upper.equals("TANK")) {
            total += 26;
        } else if (upper.equals("SHORT_SLEEVES")) {
            total += 30;
        } else if (upper.equals("LONG_SLEEVES")) {
            total += 40;
        }
        //lower
        if (lower.equals("SHORTS")) {
            total += 18;
        } else if (lower.equals("MEDIUM_PANTS")) {
            total += 25;
        } else if (lower.equals("LONG_PANTS")) {
            total += 32;
        }
        
        this.bodyExposure = 100 - total;
    }
}
