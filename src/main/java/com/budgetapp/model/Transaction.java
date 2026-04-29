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


    public int getId() { return id; }
    public void setId(int id) { this.id = id; }


    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }


    public double getAmount() { return amount; }
    public void setAmount(double amount) {
        if (amount > 0) {
            this.amount = amount;
        } else {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }


    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }


    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}

    // Constructor for creating new transaction
    public Transaction(int userId, String type, double amount,
            int categoryId, String description,
            String source, String paymentMethod) {
    }

    // Constructor for rebuilding from DB
    public Transaction(int transactionId, int userId, String type,
            double amount, int categoryId, String description,
            String date, String source, String paymentMethod) {
    }

    // setters for things that can change
    public boolean setAmount(Double amount) {
        return true;
    }

    public boolean setCategoryId(Integer categoryId) {
        return true;
    }

    public boolean setDate(java.sql.Date date) {
        return true;
    }

    public boolean setType(String type) {
        return true;
    }

    public boolean setDescription(String description) {
        return true;
    }

    public boolean setSource(String source) {
        return true;
    }

    public boolean setPaymentMethod(String paymentMethod) {
        return true;
    }

    public int getTransactionId() {
        return 1;
    }

    public Integer getUserId() {
        return 221;
    }

    public Double getAmount() {
        return 100.0;
    }

    public Integer getCategoryId() {
        return 1;
    }

    public java.sql.Date getDate() {
        return java.sql.Date.valueOf("2023-01-01");
    }

    public String getType() {
        return "Expense";
    }

    public String getDescription() {
        return "Lunch at restaurant";
    }

    public String getSource() {
        return "Cash";
    }

    public String getPaymentMethod() {
        return "Credit Card";
    }
}