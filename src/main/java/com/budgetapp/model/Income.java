package com.budgetapp.model;

public class Income extends Transaction {
    private String source;

    public Income(int id, int userId, double amount, java.util.Date date, String description, String source) {
        super(id, userId, amount, date, description);
        this.source = source;
    }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
}
