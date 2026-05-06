package com.budgetapp.model;

/**
 * Represents the security credentials and link to a user's profile. This class
 * handles sensitive data used during the authentication process.
 */
public class Account {

    /**
     * The unique email address used for login identification.
     */
    private String email;

    /**
     * The hashed version of the user's password.
     */
    private String password;

    /**
     * The foreign key link to the associated {@link User} profile.
     */
    private int userId;

    // constructor 1
    /**
     * Constructs a new Account with specified credentials and user association.
     *
     * @param e the unique email address
     * @param p the hashed password
     * @param i the unique user ID (foreign key)
     */
    public Account(String e, String p, int i) {
        email = e;
        password = p;
        userId = i;
    }

    /**
     * @return the unique ID of the associated user profile.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @return the email address associated with this account.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @return the hashed password string.
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param email the new email address for this account.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @param password the new hashed password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @param userId the ID to link this account to a specific user.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }
}
