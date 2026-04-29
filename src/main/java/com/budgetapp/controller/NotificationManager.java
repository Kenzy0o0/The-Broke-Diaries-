package com.budgetapp.controller;

import com.budgetapp.observer.IBudgetObserver;
import com.budgetapp.model.Budget;
import com.budgetapp.model.Notification;
import com.budgetapp.infrastructure.DatabaseManager;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class NotificationManager implements IBudgetObserver {

    @Override
    public void updateAlert(Budget budget) {
        String message = "Your budget limit of " + budget.getLimit() + " has been exceeded. Current spent: " + budget.getCurrentSpent();

        Notification notification = new Notification(budget.getUserId(), message);
        DatabaseManager.getInstance().saveNotification(notification);
        show(message);
    }

    private void show(String message) {
        System.out.println("--- UI ALERT DISPLAYED ---");
        System.out.println("Message: " + message);
        System.out.println("--------------------------");
    }

    public List<Notification> getUnreadNotifications(int userId) {
        List<Notification> allNotifications = DatabaseManager.getInstance().fetchNotifications(userId);
        if (allNotifications == null) {
            return new ArrayList<>();
        }
        return allNotifications.stream().filter(n -> !n.isRead()).collect(Collectors.toList());
    }

    
    public void markAsRead(int notificationId) {
    boolean success = DatabaseManager.getInstance().markNotificationRead(notificationId);
    if (success) {
        System.out.println("Notification "+ notificationId +" marked as read");
    } else {
        System.err.println("Failed to mark notification "+ notificationId +" as read");
    }
}
}