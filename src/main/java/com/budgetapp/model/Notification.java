package com.budgetapp.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents a system alert or message intended for the user. Notifications
 * track their own read status and timestamp, allowing for a persistent "inbox"
 * or alert feed within the application.
 *
 * @author WeDon'tHave
 * @version $Id: $Id
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
     * @param id a int
     * @param userId a int
     * @param message a {@link java.lang.String} object
     * @param isRead a boolean
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
     *
     * @param userId a int
     * @param message a {@link java.lang.String} object
     */
    public Notification(int userId, String message) {
        this(0, userId, message, false, new Date());
    }

    /**
     * <p>
     *Constructor for Notification.</p>
     *
     * @param id a int
     * @param userId a int
     * @param message a {@link java.lang.String} object
     * @param isRead a boolean
     * @param date a {@link java.util.Date} object
     */
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

    /**
     * <p>
     *   * Getter for the field <code>id</code>.</p>
     *
     * @return a int
     */
    public int getId() {
        return id;
    }

    /**
     * <p>
     * Setter for the field <code>id</code>.</p>
     *    *
     * @param id a int
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * <p>
     * Getter for the field <code>userId</code>.</p>
    * @return a int
     */
    public int getUserId() {
        return userId;
    }

    /**
     * <p>
                * Setter for the field <code>userId</code>.</p>
     *
     * @param userId a int
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * <p>

    * Getter for the field <code>message</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getMessage() {
        return message;
    }

    /**
     * <p>
    *
     * @param message a {@link java.lang.String} object
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * <p> isRead.</p>
     *
     * @return a boolean
     */
    public boolean isRead() {
        return isRead;
    }

    /**
     * <p>
     *  * setRead.</p>
     *
     * @param read a boolean
     */
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

    /**
     * <p>
     *   * Getter for the field <code>date</code>.</p>
     *
     * @return a {@link java.util.Date} object
     */
    public Date getDate() {
        return date;
    }

    /**
     * <p>
     *Setter for the field <code>date</code>.</p>
     *
     * @param date a {@link java.util.Date} object
     */
    public void setDate(Date date) {
        this.date = date;
    }
}
