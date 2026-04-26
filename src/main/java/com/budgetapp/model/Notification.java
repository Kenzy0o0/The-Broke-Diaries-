package com.budgetapp.model;

import java.util.Date;

public class Notification {
    private int userId;
    private String message;
    private boolean isRead;
    private Date date;

    public Notification(int userId, String message) {
        this.userId = userId;
        this.message = message;
        this.isRead = false; // Default to unread
        this.date = new Date(); // Set to current date/time
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isRead() { return isRead; }
    public void markAsRead() { this.isRead = true; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
}
