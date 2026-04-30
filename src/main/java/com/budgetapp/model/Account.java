package com.budgetapp.model;

public class Account {
    private String email;
    private String password;
    private int userId;
    public Account(String e,String p,int i){
        email=e;
        password=p;
        userId=i;
    }

    public int getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
