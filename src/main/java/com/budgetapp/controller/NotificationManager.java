package com.budgetapp.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.budgetapp.infrastructure.DatabaseManager;
import com.budgetapp.model.Budget;
import com.budgetapp.model.Notification;
import com.budgetapp.observer.IBudgetObserver;

import javafx.application.Platform;
import javafx.scene.control.Alert;

public class NotificationManager implements IBudgetObserver {

    @Override
    public void updateAlert(Budget budget) {
        String message = "Budget limit of " + budget.getLimit() + " exceeded! Current spent: " + budget.getCurrentSpent();

        Notification notification = new Notification(budget.getUserId(), message);
        DatabaseManager.getInstance().saveNotification(notification);
        show(message);
    }

    private void show(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Budget Alert");
            alert.setHeaderText("Budget Limit Exceeded!");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public List<Notification> getUnreadNotifications(int userId) {
        List<Notification> all = DatabaseManager.getInstance().fetchNotifications(userId);
        if (all == null) return new ArrayList<>();
        return all.stream().filter(n -> !n.isRead()).collect(Collectors.toList());
    }
    public void markAsRead(int notificationId) {
        DatabaseManager.getInstance().markNotificationRead(notificationId);
    }
}