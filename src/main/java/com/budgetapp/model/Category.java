package com.budgetapp.model;

public class Category {

    private int categoryId;   // ← lowercase c fixed
    private String name;
    private boolean isActive;

    public Category(int categoryId, String name, boolean isActive) {
        this.categoryId = categoryId;
        this.name = name;
        this.isActive = isActive;
    }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    public void activate() { isActive = true; }
    public void deactivate() { isActive = false; }
}