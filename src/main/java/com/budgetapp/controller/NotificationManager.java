package com.budgetapp.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.budgetapp.infrastructure.DatabaseManager;
import com.budgetapp.model.Budget;
import com.budgetapp.model.Notification;
import com.budgetapp.observer.IBudgetObserver;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Observer that listens for budget limit violations. When a budget is exceeded,
 * creates and saves a {@link com.budgetapp.model.Notification} to the database,
 * and shows a popup alert on the JavaFX UI thread.
 *
 * Implements {@link com.budgetapp.observer.IBudgetObserver}.
 */
public class NotificationManager implements IBudgetObserver {

    /** {@inheritDoc} */
    @Override
    public void updateAlert(Budget budget) {
        String message = String.format(
            "⚠ Budget limit of %.2f exceeded!\nSpent so far: %.2f",
            budget.getLimit(),
            budget.getCurrentSpent()
        );

        // Persist to DB first (safe on any thread)
        Notification notification = new Notification(budget.getUserId(), message);
        DatabaseManager.getInstance().saveNotification(notification);

        // Show UI popup on the JavaFX Application Thread
        // Platform.runLater is REQUIRED here because budget updates can be
        // triggered from a background call — touching JavaFX nodes off the
        // FX thread throws an IllegalStateException.
        Platform.runLater(() -> showPopup(message));
    }

    /**
     * Displays a styled, non-blocking notification popup anchored to the
     * top-right of the screen. It auto-dismisses after the user clicks OK,
     * or can be closed manually.
     *
     * @param message the alert text to display
     */
    private void showPopup(String message) {
        Stage popup = new Stage();

        popup.initStyle(StageStyle.DECORATED);
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Budget Alert");
        popup.setResizable(false);

        Label icon = new Label("⚠");
        icon.setStyle("-fx-font-size: 28px; -fx-text-fill: #00c2a8;");

        Label header = new Label("Budget Limit Exceeded!");
        header.setStyle(
            "-fx-font-size: 15px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #e8edf4;"
        );

        HBox headerRow = new HBox(10, icon, header);
        headerRow.setAlignment(Pos.CENTER_LEFT);

        // ── Message body ───────────────────────────────────────────────────
        Label body = new Label(message);
        body.setStyle("-fx-font-size: 13px; -fx-text-fill: #8a99b3;");
        body.setWrapText(true);
        body.setMaxWidth(320);

        // ── Dismiss button ─────────────────────────────────────────────────
        Button okBtn = new Button("OK");
        okBtn.setStyle(
            "-fx-background-color: #00c2a8;;" +
            "-fx-text-fill: #0f1923;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 7 28 7 28;" +
            "-fx-cursor: hand;"
        );
        okBtn.setOnAction(e -> popup.close());

        HBox btnRow = new HBox(okBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(14, headerRow, body, btnRow);
        root.setPadding(new Insets(20));
        root.setStyle(
            "-fx-background-color: #1a2535;" +
            "-fx-border-color: #00c2a8;" +
            "-fx-border-width: 1.5px;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;"
        );

        popup.setScene(new Scene(root, 360, 160));
        popup.show();
    }

    /**
     * Retrieves all notifications for a user that have not been marked as read.
     *
     * @param userId the unique ID of the user
     * @return a list of unread {@link Notification} objects; empty list if none
     */
    public List<Notification> getUnreadNotifications(int userId) {
        List<Notification> all = DatabaseManager.getInstance().fetchNotifications(userId);
        if (all == null) {
            return new ArrayList<>();
        }
        return all.stream()
                  .filter(n -> !n.isRead())
                  .collect(Collectors.toList());
    }

    /**
     * Updates a specific notification's status to 'read' in the database.
     *
     * @param notificationId the unique ID of the notification to update
     */
    public void markAsRead(int notificationId) {
        DatabaseManager.getInstance().markNotificationRead(notificationId);
    }
}