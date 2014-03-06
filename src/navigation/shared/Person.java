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
    private int bodyExposure; //as a percentage
    
    public Person(int skinType, int SPF, int bodyExposure) {
        this.skinType = skinType;
        this.SPF = SPF;
        this.bodyExposure = bodyExposure;
    }
    
    public int getSkinType() {
        return this.skinType;
    }

    public int getSPF() {
        return this.SPF;
    }
    
    public int getBodyExposure() {
        return this.bodyExposure;
    }

    public void changeSkinType(int skinType) {
        this.skinType = skinType;
    }

    public void changeSPF(int SPF) {
        this.SPF = SPF;
    }
    
    public void changeBodyExposure(int bodyExposure) {
        this.bodyExposure = bodyExposure;
    }
}
