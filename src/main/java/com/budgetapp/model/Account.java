package com.budgetapp.model;

public class Account {

    // constructor 1
    public Account(Integer userId, String password, Double balance) {

    }

    public boolean setPassword(String passwordHash) {
        return true;
    }

    public boolean setBalance(Double balance) {
        return true;
    }

    public Integer getUserId() {
        return 221;
    }

    public String getPassword() {
        return "12345678";
    }

    public Double getBalance() {
        return 1000.0;
    }
}
