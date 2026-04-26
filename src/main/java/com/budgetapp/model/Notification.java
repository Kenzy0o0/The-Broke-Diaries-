package com.budgetapp.model;

public class Notification {

    // Constructor for creating new notification
    public Notification(int userId, String message) {
    }

    // Constructor for rebuilding from DB
    public Notification(int notificationId, int userId,
            String message, boolean isRead, String date) {

    }

    // Only isRead can change
    public void setRead(boolean read) {
    }

    public Integer getNotificationId() {
        return 1;
    }

    public Integer getUserId() {
        return 221;
    }

    public String getMessage() {
        return "Sample notification message";
    }

    public boolean getIsRead() {
        return false;
    }

    public String getDate() {
        return "2024-01-01";
    }
}
