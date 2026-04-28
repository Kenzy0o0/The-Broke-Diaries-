package com.budgetapp.controller;

import com.budgetapp.observer.IBudgetObserver;
import com.budgetapp.model.Budget;
import com.budgetapp.model.Notification;
import com.budgetapp.infrastructure.DatabaseManager;
import java.util.List;


public class NotificationManager implements IBudgetObserver {
    
    @Override
    public void updateAlert(Budget budget) {
        String message = "Your budget limit of " + budget.getLimit() +" has been exceeded. Current spent: " + budget.getCurrentSpent();

        Notification notification = new Notification(budget.getUserId(), message);
        DatabaseManager.getInstance().saveNotification(notification);

        show(message);
    }
    private void show(String message) {
        // This simulates the UI layer picking up the alert
        System.out.println("--- UI ALERT DISPLAYED ---");
        System.out.println("Message: " + message);
        System.out.println("--------------------------");
    }

    public List<Notification> getunreadNotifications(int userId) {
        // Implementation to retrieve unread notifications for a user
        return new ArrayList<>(); // Placeholder return
    }
}