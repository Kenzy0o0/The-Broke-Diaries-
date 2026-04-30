package com.budgetapp.model;

public class Category {

    private int CategoryId;
    private String name;
    // private String type;
    private boolean isActive;

    public Category(int CategoryId, String name, boolean isActive) {
        this.CategoryId = CategoryId;
        this.name = name;
        // this.type = type;
        this.isActive = isActive;
    }

    // public Category(int CategoryId, String name, boolean isActive) {
    //     this(CategoryId, name, true);
    // }
    public int getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(int CategoryId) {
        this.CategoryId = CategoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // public String getType() { return type; }
    // public void setType(String type) { this.type = type; }
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void activate() {
        isActive = true;
    }

    public void deactivate() {
        isActive = false;
    }
}
