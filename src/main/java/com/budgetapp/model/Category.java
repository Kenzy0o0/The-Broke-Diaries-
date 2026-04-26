package com.budgetapp.model;

public class Category {

    // Constructor for creating new category
    public Category(String name, String type) {
    }

    // Constructor for rebuilding from DB
    public Category(int categoryId, String name, String type) {
    }

    // setters for things that can change
    public boolean setName(String name) {
        return true;
    }

    public boolean setType(String type) {
        return true;
    }

    public Integer getCategoryId() {
        return 1;
    }

    public String getName() {
        return "Sample Category";
    }

    public String getType() {
        return "Sample Type";
    }
}
