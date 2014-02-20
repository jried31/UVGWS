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
    private String skinType;
    private int SPF;
    
    public Person(String skinType, int SPF) {
        this.skinType = skinType;
        this.SPF = SPF;
    }
    
    public String getSkinType() {
        return this.skinType;
    }

    public int getSPF() {
        return this.SPF;
    }

    public void changeSkinType(String skinType) {
        this.skinType = skinType;
    }

    public void changeSPF(int SPF) {
        this.SPF = SPF;
    }
}
