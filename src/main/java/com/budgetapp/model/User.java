package com.budgetapp.model;

public class User {
    private int id;
    private String Name;
    private double Balance;
    private String Currency;
    public User(int i, String n,double b,String c){
        id=i;
        Name=n;
        Balance=b;
        Currency=c;
    }

    public boolean updateBalance(double amount){
        if(Balance+amount>=0){
            Balance+=amount;
            return true;
        }
        return false;
    }
    
}
