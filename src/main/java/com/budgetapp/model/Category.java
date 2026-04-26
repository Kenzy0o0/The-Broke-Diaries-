package com.budgetapp.model;

public class Category {
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
