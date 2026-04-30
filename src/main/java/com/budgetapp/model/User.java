package com.budgetapp.model;

public class User {
    private int id;
    private String name;
    private double balance;
    private String currency;
    public User(int i, String n,double b,String c){
        id=i;
        name=n;
        balance=b;
        currency=c;
    }

    public boolean updateBalance(double amount){
        if(balance+amount>=0){
            balance+=amount;
            return true;
        }
        return false;
    }

    public double getBalance() {
        return balance;
    }

    public String getCurrency() {
        return currency;
    }

    public String getName() {
        return name;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
