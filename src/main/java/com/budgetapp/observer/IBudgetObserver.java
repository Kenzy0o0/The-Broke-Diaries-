package com.budgetapp.observer;

import com.budgetapp.model.Budget;

/**
 * Observer interface for budget limit alerts. Any class that wants to receive
 * budget exceeded notifications must implement this interface.
 *
 * @version 1.0
 */
public interface IBudgetObserver {

    void updateAlert(Budget budget);
}
