package com.project.models;

import java.util.Date;

/**
 * Represents an event with details such as name, description, date, location, and more.
 */
public class Event {
    private int id;
    private String name;
    private String description;
    private Date date;
    private String location;
    private String college;
    private int createdBy;
    private String posterPath;
    private String approvalStatus; // Field for registration approval status
    private String errorData; // Field for storing error details if applicable

    /**
     * Full constructor with all fields including approvalStatus and errorData.
     */
    public Event(int id, String name, String description, Date date, String location, String college, int createdBy, String posterPath, String approvalStatus, String errorData) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.location = location;
        this.college = college;
        this.createdBy = createdBy;
        this.posterPath = posterPath;
        this.approvalStatus = approvalStatus;
        this.errorData = errorData;
    }

    /**
     * Constructor without errorData, defaults errorData to null.
     */
    public Event(int id, String name, String description, Date date, String location, String college, int createdBy, String posterPath, String approvalStatus) {
        this(id, name, description, date, location, college, createdBy, posterPath, approvalStatus, null);
    }

    /**
     * Constructor without approvalStatus and errorData, defaults them to null.
     */
    public Event(int id, String name, String description, Date date, String location, String college, int createdBy, String posterPath) {
        this(id, name, description, date, location, college, createdBy, posterPath, null, null);
    }

    /**
     * Constructor without college, approvalStatus, and errorData fields.
     */
    public Event(int id, String name, String description, Date date, String location, int createdBy, String posterPath) {
        this(id, name, description, date, location, null, createdBy, posterPath, null, null);
    }

    /**
     * Constructor for approved events with essential fields only.
     */
    public Event(int id, String name, Date date, String location) {
        this(id, name, null, date, location, null, 0, null, "approved", null);
    }

    /**
     * Constructor for events with approval status and errorData, for use in registration.
     */
    public Event(int id, String name, String description, Date date, String location, String approvalStatus) {
        this(id, name, description, date, location, null, 0, null, approvalStatus, null);
    }

    // Getters and Setters with added comments

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name != null ? name : "Unnamed Event"; // Optional handling for null
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description != null ? description : "No description available.";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLocation() {
        return location != null ? location : "Location not specified";
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCollege() {
        return college != null ? college : "No college specified";
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getPosterPath() {
        return posterPath != null ? posterPath : "resources/images/default_poster.jpg"; // Default image if null
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getApprovalStatus() {
        return approvalStatus != null ? approvalStatus : "Pending"; // Default approval status
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getErrorData() {
        return errorData != null ? errorData : ""; // Return empty string if errorData is null
    }

    public void setErrorData(String errorData) {
        this.errorData = errorData;
    }

    // Optional: toString method to display event information
    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", date=" + date +
                ", location='" + getLocation() + '\'' +
                ", college='" + getCollege() + '\'' +
                ", createdBy=" + createdBy +
                ", posterPath='" + getPosterPath() + '\'' +
                ", approvalStatus='" + getApprovalStatus() + '\'' +
                ", errorData='" + getErrorData() + '\'' +
                '}';
    }
}
