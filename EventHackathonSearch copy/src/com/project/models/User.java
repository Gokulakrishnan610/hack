package com.project.models;

/**
 * Represents a user in the system, including details like role, college, email, and approval status.
 */
public class User {
    private int id;
    private String username;
    private String password;
    private String role;
    private String college;
    private String email;
    private String approvalStatus;

    /**
     * Constructor to initialize a User instance.
     *
     * @param id             User ID
     * @param username       Username for login
     * @param password       User's password
     * @param role           User's role (e.g., admin, student, organizer)
     * @param college        College associated with the user
     * @param email          Email address of the user
     * @param approvalStatus Approval status (e.g., pending, approved, rejected)
     */
    public User(int id, String username, String password, String role, String college, String email, String approvalStatus) {
        this.id = id;
        this.username = username;
        this.password = password;
        setRole(role);
        this.college = college;
        this.email = email;
        this.approvalStatus = approvalStatus;
    }

    // Getters and Setters with comments

    /**
     * Gets the user ID.
     * @return the user ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the user ID.
     * @param id the user ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the username.
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password.
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the user's role.
     * @return the role (e.g., admin, student, organizer)
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets the user's role with validation.
     * @param role the role to set (e.g., admin, student, organizer)
     * @throws IllegalArgumentException if the role is invalid
     */
    public void setRole(String role) {
        if (role.equalsIgnoreCase("admin") || role.equalsIgnoreCase("student") || role.equalsIgnoreCase("organizer")) {
            this.role = role;
        } else {
            throw new IllegalArgumentException("Invalid role. Must be 'admin', 'student', or 'organizer'.");
        }
    }

    /**
     * Gets the college associated with the user.
     * @return the college name
     */
    public String getCollege() {
        return college != null ? college : "Not specified";
    }

    /**
     * Sets the college associated with the user.
     * @param college the college to set
     */
    public void setCollege(String college) {
        this.college = college;
    }

    /**
     * Gets the email of the user.
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the user.
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the approval status of the user.
     * @return the approval status (e.g., pending, approved, rejected)
     */
    public String getApprovalStatus() {
        return approvalStatus != null ? approvalStatus : "Pending";
    }

    /**
     * Sets the approval status of the user.
     * @param approvalStatus the approval status to set
     */
    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
}
