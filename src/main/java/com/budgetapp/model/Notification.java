package com.budgetapp.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a system alert or message intended for the user. Notifications
 * track their own read status and timestamp, allowing for a persistent "inbox"
 * or alert feed within the application.
 */
public class Notification {

    /**
     * Unique database identifier for the notification.
     */
    private int id;

    /**
     * The ID of the recipient user.
     */
    private int userId;

    /**
     * The actual text content of the alert (e.g., "Budget limit reached").
     */
    private String message;

    /**
     * Tracks whether the user has acknowledged or viewed the notification.
     */
    private boolean isRead;

    /**
     * The timestamp of when the notification was generated.
     */
    private Date date;

    /**
     * Full constructor using a String date, typically for database
     * reconstruction.
     *
     * @param dateStr the timestamp in "yyyy-MM-dd HH:mm:ss" format.
     */
    public Notification(int id, int userId, String message, boolean isRead, String dateStr) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.isRead = isRead;
        this.date = parseDate(dateStr);
    }

    /**
     * Convenience constructor for creating a brand-new notification in
     * real-time. Defaults to ID 0, unread status, and current system time.
     */
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

    /**
     * Internal helper to safely parse date strings from the database. Defaults
     * to the current date if parsing fails to prevent null pointer exceptions.
     */
    private static Date parseDate(String dateStr) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    /**
     * Updates the status of the notification to 'read'. Syntactic sugar for
     * setRead(true).
     */
    public void markAsRead() {
        isRead = true;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
