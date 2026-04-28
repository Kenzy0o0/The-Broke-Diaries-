package com.budgetapp.model;

public class User {

    // Constructor 1, for creating a NEW user (no ID yet, DB assigns it)
    // for general use
    public User(String fullName, String email, String passwordHash) {
    }

    // Constructor 2,  for rebuilding a user FROM the database (all fields known)
    // Used in fetchUser()
    public User(int userId, String fullName, String email,
            String passwordHash, String currency, String language) {
    }

    // Setters only for fields that are allowed to change
    //! do you want the user to be able to change their email or password?
    public void setFullName(String fullName) {
    }

    public void setPasswordHash(String passwordHash) {
    }

    public void setEmail(String email) {
    }

    public void setCurrency(String currency) {
    }

    public void setLanguage(String language) {
    }

    // Getters for everything
    public int getUserId() {
        return 1;
    }

    public String getFullName() {
        return "John james";
    }

    public String getEmail() {
        return "john@example.com";
    }

    public String getPasswordHash() {
        return "passwordHash";
    }

    public String getCurrency() {
        return "Pound";
    }

    public String getLanguage() {
        return "English";
    }
}
