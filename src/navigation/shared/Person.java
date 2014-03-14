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
    
    private int skinType;
    private int SPF;
    private double bodyExposure; //as a percentage, 0-100, 0 = fully covered, 100 = no coverage
    
    public Person(String skinType, String SPF, double bodyExposure) {
        
        this.skinType = Integer.parseInt(skinType.substring(5));
        this.SPF = Integer.parseInt(SPF);
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

    public void setSkinType(int skinType) {
        this.skinType = skinType;
    }

    public void setSPF(int SPF) {
        this.SPF = SPF;
    }
    
    public void setBodyExposure(double bodyExposure) {
        this.bodyExposure = bodyExposure;
    }
    
    public void clothesToBodyExposure(String head, String upper, String lower) {
        double total = 0;
        //head
        if (head.equals("HAT")) {
            total += 1.5;
        } //else if (head.equals("SUNGLASS")) {
            //total += 1;
        //}
        //upper
        if (upper.equals("SLEEVES-SPORTSBRA")) {
            total += 13;
        } else if (upper.equals("SLEEVES-TANK")) {
            total += 26;
        } else if (upper.equals("SLEEVES-SHORT")) {
            total += 30;
        } else if (upper.equals("SLEEVES-LONG")) {
            total += 40;
        }
        //lower
        if (lower.equals("PANTS-SHORTS")) {
            total += 18;
        } else if (lower.equals("PANTS-MEDIUM")) {
            total += 25;
        } else if (lower.equals("PANTS-LONG")) {
            total += 32;
        }
        
        this.bodyExposure = 100 - total;
    }
}
