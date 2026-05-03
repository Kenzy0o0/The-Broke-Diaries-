package com.budgetapp.model;

import java.util.Date;

public class Expense extends Transaction {

    private String paymentMethod;

    public Expense(int id, int userId,int categoryId, double amount, Date date, String description,  String paymentMethod) {
        super(id, userId, categoryId, amount, date, description);
        this.paymentMethod = paymentMethod;
    }

    public Expense(int userId, double amount, Date date, String description,
            int categoryId, String source, String paymentMethod) {
        this(0, userId, categoryId, amount, date, description, paymentMethod);
    }

    public Expense(int id, int userId, int categoryId, double amount, String dateStr, String description, String paymentMethod) {
        this(id, userId, categoryId, amount, java.sql.Date.valueOf(dateStr), description, paymentMethod);
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
