package com.budgetapp.model;

public class Category {
    
}
private int Id;
    private String name;
    private boolean isActive; // To track if the category is active[for expenses] or not

    public Category( String name) {
        this.name = name;
        this.isActive = true; // Default to active when created
    }

    public int getId() { return Id; }
    public void setId(int Id) { this.Id = Id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isActive() { return isActive; }
    public void deactivate() { this.isActive = false; }
    public void activate()   { this.isActive = true;  }
}
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
