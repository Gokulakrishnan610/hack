package com.project.services;

import com.project.models.User;
import com.project.utils.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    /**
     * Authenticates a user based on username, password, and role.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @param role     The role of the user (e.g., 'admin', 'student', 'organizer').
     * @return A User object if authentication is successful; null otherwise.
     */
    public User authenticateUser(String username, String password, String role) {
        String query = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ? AND approval_status = 'approved'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapUser(rs);
            }
        } catch (SQLException e) {
            logError("Error authenticating user", e);
        }
        return null; // Return null if authentication fails
    }

    /**
     * Retrieves all organizers with pending approval status.
     *
     * @return A list of User objects representing organizers with pending approval.
     */
    public List<User> getPendingOrganizers() {
        List<User> organizers = new ArrayList<>();
        String query = "SELECT * FROM users WHERE role = 'organizer' AND approval_status = 'pending'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                organizers.add(mapUser(rs));
            }
        } catch (SQLException e) {
            logError("Error retrieving pending organizers", e);
        }
        return organizers;
    }

    /**
     * Approves or rejects a pending organizer registration.
     *
     * @param organizerId The ID of the organizer to update.
     * @param status      The new approval status ('approved' or 'rejected').
     * @return True if the update was successful; false otherwise.
     */
    public boolean updateOrganizerApprovalStatus(int organizerId, String status) {
        String query = "UPDATE users SET approval_status = ? WHERE id = ? AND role = 'organizer'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, status);
            stmt.setInt(2, organizerId);
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            logError("Error updating organizer approval status", e);
            return false;
        }
    }

    /**
     * Retrieves all users by their role.
     *
     * @param role The role of the users to retrieve.
     * @return A list of User objects with the specified role.
     */
    public List<User> getUsersByRole(String role) {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users WHERE role = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, role);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                users.add(mapUser(rs));
            }
        } catch (SQLException e) {
            logError("Error retrieving users by role", e);
        }
        return users;
    }

    /**
     * Maps a ResultSet row to a User object.
     *
     * @param rs The ResultSet containing user data.
     * @return A User object populated with data from the ResultSet.
     * @throws SQLException if there is an issue accessing the ResultSet.
     */
    private User mapUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("id"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("role"),
                rs.getString("college"),
                rs.getString("email"),
                rs.getString("approval_status")
        );
    }

    /**
     * Logs an error message and stack trace for SQLExceptions.
     *
     * @param message A message describing the context of the error.
     * @param e       The SQLException that was thrown.
     */
    private void logError(String message, SQLException e) {
        System.err.println(message);
        e.printStackTrace();
    }
}
