package com.budgetapp.observer;

import com.budgetapp.model.Budget;

public interface IBudgetObserver {
    void updateAlert(Budget budget);
}