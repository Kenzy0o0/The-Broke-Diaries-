package com.budgetapp.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.budgetapp.infrastructure.DatabaseManager;
import com.budgetapp.model.Budget;
import com.budgetapp.model.Notification;
import com.budgetapp.observer.IBudgetObserver;

/**
 * Observer that listens for budget limit violations. When a budget is exceeded,
 * creates and saves a {@link com.budgetapp.model.Notification} to the database.
 *
 * Implements {@link com.budgetapp.observer.IBudgetObserver}.
 *
 * @version 1.0
 * @author WeDon'tHave
 */
public class NotificationManager implements IBudgetObserver {

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateAlert(Budget budget) {
        String message = "Budget limit of " + budget.getLimit() + " exceeded! Current spent: " + budget.getCurrentSpent();

        Notification notification = new Notification(budget.getUserId(), message);
        DatabaseManager.getInstance().saveNotification(notification);
        show(message);
    }

    /**
     * Helper method to simulate a UI alert for the user. In a production
     * environment, this would trigger a push notification or UI toast.
     *
     * @param message the alert text to display
     */
    private void show(String message) {
        System.out.println("--- UI ALERT DISPLAYED ---");
        System.out.println("Message: " + message);
        System.out.println("--------------------------");
    }

    /**
     * Retrieves all notifications for a specific user that haven't been marked
     * as read.
     *
     * @param userId the unique ID of the user
     * @return a list of unread {@link com.budgetapp.model.Notification}
     * objects; returns an empty list if none found
     */
    public List<Notification> getUnreadNotifications(int userId) {
        List<Notification> all = DatabaseManager.getInstance().fetchNotifications(userId);
        if (all == null) {
            return new ArrayList<>();
        }
        return all.stream().filter(n -> !n.isRead()).collect(Collectors.toList());
    }

    /**
     * Updates the status of a specific notification to 'read' in the database.
     *
     * @param notificationId the unique ID of the notification to update
     */
    public void markAsRead(int notificationId) {
        DatabaseManager.getInstance().markNotificationRead(notificationId);
    }
}
