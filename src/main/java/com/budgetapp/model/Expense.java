package com.budgetapp.model;

public class Expense extends Transaction {
    private String paymentMethod;

    public Expense(int id, int userId, double amount, java.util.Date date, String description, String paymentMethod) {
        super(id, userId, amount, date, description);
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
}
