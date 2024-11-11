package com.project.services;

import com.project.models.Registration;
import com.project.models.Event;
import com.project.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistrationService {

    /**
     * Registers a student for an event. Sets the default approval status to 'pending'.
     *
     * @param userId  The ID of the user registering for the event.
     * @param eventId The ID of the event.
     * @return True if the registration was successful, false otherwise.
     */
    public boolean registerForEvent(int userId, int eventId) {
        String query = "INSERT INTO registrations (user_id, event_id, approval_status) VALUES (?, ?, 'pending')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, eventId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if a user is already registered for a specific event.
     *
     * @param userId  The ID of the user.
     * @param eventId The ID of the event.
     * @return True if the user is already registered, false otherwise.
     */
    public boolean isAlreadyRegistered(int userId, int eventId) {
        String query = "SELECT COUNT(*) FROM registrations WHERE user_id = ? AND event_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, eventId);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates the approval status of a specific registration.
     *
     * @param registrationId The ID of the registration.
     * @param status         The new approval status (e.g., 'approved' or 'rejected').
     * @return True if the update was successful, false otherwise.
     */
    public boolean updateRegistrationStatus(int registrationId, String status) {
        String query = "UPDATE registrations SET approval_status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, status);
            stmt.setInt(2, registrationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all pending registrations for a specific college.
     *
     * @param college The name of the college to filter registrations by.
     * @return A list of Registration objects representing pending registrations for the specified college.
     */
    public List<Registration> getPendingRegistrationsByCollege(String college) {
        List<Registration> registrations = new ArrayList<>();
        String query = "SELECT r.id, r.user_id, r.event_id, r.registration_date, r.approval_status, u.name AS student_name " +
                       "FROM registrations r " +
                       "JOIN events e ON r.event_id = e.id " +
                       "JOIN users u ON r.user_id = u.id " +
                       "WHERE e.college = ? AND r.approval_status = 'pending'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, college);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Registration registration = new Registration(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("event_id"),
                        rs.getTimestamp("registration_date"),
                        rs.getString("approval_status"),
                        rs.getString("student_name")
                );
                registrations.add(registration);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return registrations;
    }

    /**
     * Retrieves all registrations for a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of Registration objects representing all registrations for the specified user.
     */
    public List<Registration> getRegistrationsByUser(int userId) {
        List<Registration> registrations = new ArrayList<>();
        String query = "SELECT r.*, u.name AS student_name FROM registrations r " +
                       "JOIN users u ON r.user_id = u.id " +
                       "WHERE r.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                registrations.add(new Registration(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getInt("event_id"),
                        rs.getTimestamp("registration_date"),
                        rs.getString("approval_status"),
                        rs.getString("student_name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return registrations;
    }

    /**
     * Unregisters a student from an event.
     *
     * @param userId  The ID of the user.
     * @param eventId The ID of the event.
     * @return True if the unregistration was successful, false otherwise.
     */
    public boolean unregisterFromEvent(int userId, int eventId) {
        String query = "DELETE FROM registrations WHERE user_id = ? AND event_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, eventId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves events that a user has registered for, including their approval status.
     *
     * @param userId The ID of the user.
     * @return A list of Event objects representing the events the user has registered for.
     */
    public List<Event> getRegisteredEventsByUser(int userId) {
        List<Event> events = new ArrayList<>();
        String query = "SELECT e.id, e.event_name, e.event_date, e.location, r.approval_status " +
                       "FROM registrations r " +
                       "JOIN events e ON r.event_id = e.id " +
                       "WHERE r.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                events.add(new Event(
                        rs.getInt("id"),
                        rs.getString("event_name"),
                        null,
                        rs.getDate("event_date"),
                        rs.getString("location"),
                        null,
                        0,
                        null,
                        rs.getString("approval_status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    /**
     * Retrieves only approved events for a specific user.
     *
     * @param userId The ID of the user.
     * @return A list of Event objects representing the approved events the user has registered for.
     */
    public List<Event> getApprovedEventsByUser(int userId) {
        List<Event> events = new ArrayList<>();
        String query = "SELECT e.id, e.event_name, e.event_date, e.location " +
                       "FROM registrations r " +
                       "JOIN events e ON r.event_id = e.id " +
                       "WHERE r.user_id = ? AND r.approval_status = 'approved'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                events.add(new Event(
                        rs.getInt("id"),
                        rs.getString("event_name"),
                        null,
                        rs.getDate("event_date"),
                        rs.getString("location"),
                        null,
                        0,
                        null,
                        "approved"
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }
}
