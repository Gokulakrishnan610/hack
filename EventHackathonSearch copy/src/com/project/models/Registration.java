package com.project.models;

import java.util.Date;

/**
 * Represents a registration entry for an event, including student and event details.
 */
public class Registration {
    private int id;
    private int userId;
    private int eventId;
    private Date registrationDate;
    private String approvalStatus;
    private String studentName; // Optional field for the student's name

    /**
     * Full Constructor for creating a Registration instance with all fields.
     *
     * @param id               Registration ID
     * @param userId           User ID of the student
     * @param eventId          Event ID for which registration is done
     * @param registrationDate Date of registration
     * @param approvalStatus   Approval status of the registration (e.g., pending, approved, rejected)
     * @param studentName      Name of the student
     */
    public Registration(int id, int userId, int eventId, Date registrationDate, String approvalStatus, String studentName) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.registrationDate = registrationDate;
        this.approvalStatus = approvalStatus;
        this.studentName = studentName;
    }

    // Getters and Setters with added comments

    /**
     * Gets the registration ID.
     * @return the registration ID
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the registration ID.
     * @param id the registration ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the user ID of the student.
     * @return the student’s user ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Sets the user ID of the student.
     * @param userId the user ID to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Gets the event ID associated with the registration.
     * @return the event ID
     */
    public int getEventId() {
        return eventId;
    }

    /**
     * Sets the event ID associated with the registration.
     * @param eventId the event ID to set
     */
    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    /**
     * Gets the registration date.
     * @return the date of registration
     */
    public Date getRegistrationDate() {
        return registrationDate;
    }

    /**
     * Sets the registration date.
     * @param registrationDate the date to set
     */
    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }

    /**
     * Gets the approval status of the registration.
     * @return the approval status (e.g., pending, approved, rejected)
     */
    public String getApprovalStatus() {
        return approvalStatus != null ? approvalStatus : "Pending";
    }

    /**
     * Sets the approval status of the registration.
     * @param approvalStatus the approval status to set
     */
    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    /**
     * Gets the name of the student.
     * @return the student’s name
     */
    public String getStudentName() {
        return studentName != null ? studentName : "Unknown Student";
    }

    /**
     * Sets the name of the student.
     * @param studentName the student name to set
     */
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
