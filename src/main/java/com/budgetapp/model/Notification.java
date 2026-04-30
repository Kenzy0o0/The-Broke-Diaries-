package com.budgetapp.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Notification {
    private int id;
    private int userId;
    private String message;
    private boolean isRead;
    private Date date;

    
    public Notification(int id, int userId, String message, boolean isRead, String dateStr) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.isRead = isRead;
        this.date = parseDate(dateStr);
    }

    
    public Notification(int userId, String message) {
        this(0, userId, message, false, new Date());
    }

    
    public Notification(int id, int userId, String message, boolean isRead, Date date) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.isRead = isRead;
        this.date = date;
    }

    private static Date parseDate(String dateStr) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public void markAsRead() { isRead = true; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
}