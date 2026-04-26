package com.budgetapp.model;

public class Account {

    public boolean setAccountId(Integer accountId) {
        return true;
    }

    public boolean setUserId(Integer userId) {
        return true;
    }

    public boolean setPasswordHash(String passwordHash) {
        return true;
    }

    public boolean setBalance(Double balance) {
        return true;
    }

    public Integer getAccountId() {
        return 221;
    }

    public Integer getUserId() {
        return 221;
    }

    public String getPasswordHash() {
        return "12345678";
    }

    public Double getBalance() {
        return 1000.0;
    }
}
