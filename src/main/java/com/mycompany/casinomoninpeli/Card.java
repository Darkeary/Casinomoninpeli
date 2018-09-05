/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.casinomoninpeli;

/**
 *
 * @author Anders
 */
public class Card {
    private int value ;
    private int value2=0;
    private String type;
    Card(){
        System.out.print("no card created, missing generated number.");
    }
    Card(int recvalue){
        setValue(recvalue);
        
        System.out.print("card created ("+recvalue+").");
    }
    Card(int recvalue,String type){
        setValue(recvalue);
        setType(type);
        System.out.print("card created ("+recvalue+")("+type+").");
    }
    Card(int recvalue,int recvalue2,String type){
        setValue(recvalue);
        setValue(recvalue2);
        setType(type);
        System.out.print("card created ("+recvalue+")("+type+").");
    }
    
    public int getValue(){
        return this.value;
    };
    public int getType(){
        return this.type;
    };
    public void setValue(int newValue){
        this.value=newValue;
    };
    public void setValue2(int newValue){
        this.value2=newValue;
    };

    private void setType(String newType) {
        this.type=newType;
    };
}
