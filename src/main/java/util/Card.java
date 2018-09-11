/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 * @author Anders
 */
public class Card {
    private int value = 0;
    private int value2 = 0;
    private String type = "";

    public Card(int recvalue, String type) {
        setValue(recvalue);
        setType(type);
    }

    public Card(int recvalue, int recvalue2, String type) {

        setValue(recvalue);
        setValue(recvalue2);
        setType(type);
    }

    public int getValue() {

        return this.value;
    }

    ;

    public void setValue(int newValue) {
        this.value = newValue;
    }

    ;

    public int getValue2() {
        return this.value2;
    }

    ;

    public void setValue2(int newValue) {
        this.value2 = newValue;
    }

    ;

    public String getType() {
        return this.type;
    }

    ;

    private void setType(String newType) {
        this.type = newType;
    }

    ;
}
