package com.budgetapp.model;

import java.util.Date;

public class Income extends Transaction {


    private String source;

    public Income(int id, int userId,int categoryId, double amount, Date date, String description, String source) {
        super(id, userId, categoryId, amount, date, description);
        this.source = source;
    }

    public Income(int userId, double amount, Date date, String description, String source, int categoryId) {
        this(0, userId, categoryId, amount, date, description, source);
    }

    public Income(int id, int userId, int categoryId, double amount, String dateStr, String description, String source) {
            this(id, userId, categoryId, amount, java.sql.Date.valueOf(dateStr), description, source);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getType() {
        return "income";
    }
}
