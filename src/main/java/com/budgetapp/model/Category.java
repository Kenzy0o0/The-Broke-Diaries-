package com.budgetapp.model;

/**
 * Defines a classification for financial transactions. Categories can be
 * toggled as active or inactive to manage user options without deleting
 * historical transaction data.
 */
public class Category {

    /**
     * Unique identifier for the category.
     */
    private int CategoryId;

    /**
     * The display name of the category (e.g., "Utilities", "Dining Out").
     */
    private String name;

    /**
     * Flag indicating if the category is currently available for selection.
     * Inactive categories are preserved for historical reporting.
     */
    private boolean isActive;

    /**
     * Constructs a new Category with a specific ID, name, and status.
     *
     * @param CategoryId the unique database ID
     * @param name the descriptive name
     * @param isActive the initial availability status
     */
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

    /**
     * @return the unique ID of this category.
     */
    public void setCategoryId(int CategoryId) {
        this.CategoryId = CategoryId;
    }

    /**
     * @return the display name of this category.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // public String getType() { return type; }
    // public void setType(String type) { this.type = type; }
    /**
     * @return true if the category is active, false otherwise.
     */
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * Sets the category status to true. Useful for restoring a previously
     * disabled category.
     */
    public void activate() {
        isActive = true;
    }

    /**
     * Sets the category status to false. Use this instead of deletion to
     * maintain database integrity for existing transactions.
     */
    public void deactivate() {
        isActive = false;
    }
}
