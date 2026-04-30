package com.budgetapp.model;

import java.util.Date;

public abstract class Transaction {

    private int id;
    private int userId;
    private double amount;
    private Date date;
    private String description;

    public Transaction(int id, int userId, double amount, Date date, String description) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if (amount > 0) {
            this.amount = amount;
        } else {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
