package com.budgetapp.model;

import java.util.Date;

public class Income extends Transaction {
    private int categoryId;
    private String source;

    public Income(int id, int userId, double amount, Date date, String description, String source, int categoryId) {
        super(id, userId, amount, date, description);
        this.categoryId = categoryId;
        this.source = source;
    }

    public Income(int userId, double amount, Date date, String description, String source, int categoryId) {
        this(0, userId, amount, date, description, source, categoryId);
    }

    public Income(int id, int userId, double amount, String dateStr, String description, String source, int categoryId) {
        this(id, userId, amount, java.sql.Date.valueOf(dateStr), description, source, categoryId);
    }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getType() { return "income"; }
}