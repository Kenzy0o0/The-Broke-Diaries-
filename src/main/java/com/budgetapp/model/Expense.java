package com.budgetapp.model;

import java.util.Date;

public class Expense extends Transaction {

    private int categoryId;
    private String paymentMethod;

    public Expense(int id, int userId, double amount, Date date, String description, int categoryId, String paymentMethod) {
        super(id, userId, amount, date, description);
        this.categoryId = categoryId;
        this.paymentMethod = paymentMethod;
    }

    public Expense(int userId, double amount, Date date, String description,
            int categoryId, String source, String paymentMethod) {
        this(0, userId, amount, date, description, categoryId, paymentMethod);
    }

    // DB constructor with String date
    public Expense(int id, int userId, double amount, String dateStr, String description,
            int categoryId, String source, String paymentMethod) {
        this(id, userId, amount, java.sql.Date.valueOf(dateStr), description,
                categoryId, paymentMethod);
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getType() {
        return "expense";
    }
}
