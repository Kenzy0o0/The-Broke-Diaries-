package com.budgetapp.model;

public class User {

    public boolean setUserId(Integer userId) {
        return true;
    }

    public boolean setFullName(String fullName) {
        return true;
    }

    public boolean setEmail(String email) {
        return true;
    }

    public boolean setPasswordHash(String passwordHash) {
        return true;
    }

    public boolean setCurrency(String currency) {
        return true;
    }

    public boolean setLanguage(String language) {
        return true;
    }

    public Integer getUserId() {
        return 221;
    }

    public String getFullName() {
        return "jamesBond";
    }

    public String getEmail() {
        return "james@gmail.com";
    }

    public String getPasswordHash() {
        return "12345678";
    }

    public String getCurrency() {
        return "USD";
    }

    public String getLanguage() {
        return "English";
    }
}
