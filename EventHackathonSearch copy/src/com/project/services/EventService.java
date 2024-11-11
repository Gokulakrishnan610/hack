package com.project.services;

import com.project.models.Event;
import com.project.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventService {

    /**
     * Fetches all events by a specified college.
     * 
     * @param college The name of the college to filter events by.
     * @return A list of Event objects associated with the specified college.
     */
    public List<Event> getEventsByCollege(String college) {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT *, COALESCE(event_date, STR_TO_DATE(error_data, '%b %d, %Y')) AS actual_date FROM events WHERE college = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             
            stmt.setString(1, college);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Event event = mapResultSetToEvent(rs);
                events.add(event);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching events by college: " + e.getMessage());
            e.printStackTrace();
        }
        return events;
    }

    /**
     * Fetches all events without any college filter.
     * 
     * @return A list of all Event objects in the database.
     */
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT *, COALESCE(event_date, STR_TO_DATE(error_data, '%b %d, %Y')) AS actual_date FROM events";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Event event = mapResultSetToEvent(rs);
                events.add(event);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all events: " + e.getMessage());
            e.printStackTrace();
        }
        return events;
    }

    /**
     * Adds a new event to the database.
     * 
     * @param event The Event object containing the details of the event to add.
     * @return True if the event was added successfully, otherwise false.
     */
    public boolean addEvent(Event event) {
        String sql = "INSERT INTO events (event_name, event_description, event_date, location, college, created_by, event_poster, error_data) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, event.getName());
            stmt.setString(2, event.getDescription());
            stmt.setDate(3, event.getDate() != null ? new java.sql.Date(event.getDate().getTime()) : null);
            stmt.setString(4, event.getLocation());
            stmt.setString(5, event.getCollege());
            stmt.setInt(6, event.getCreatedBy());
            stmt.setString(7, event.getPosterPath());
            stmt.setString(8, event.getErrorData());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding event: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing event in the database.
     * 
     * @param event The Event object containing updated details of the event.
     * @return True if the event was updated successfully, otherwise false.
     */
    public boolean updateEvent(Event event) {
        String sql = "UPDATE events SET event_name = ?, event_description = ?, event_date = ?, location = ?, event_poster = ?, error_data = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, event.getName());
            stmt.setString(2, event.getDescription());
            stmt.setDate(3, event.getDate() != null ? new java.sql.Date(event.getDate().getTime()) : null);
            stmt.setString(4, event.getLocation());
            stmt.setString(5, event.getPosterPath());
            stmt.setString(6, event.getErrorData());
            stmt.setInt(7, event.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating event: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes an event from the database by its ID.
     * 
     * @param eventId The ID of the event to delete.
     * @return True if the event was deleted successfully, otherwise false.
     */
    public boolean deleteEvent(int eventId) {
        String sql = "DELETE FROM events WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting event: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Helper method to map a ResultSet row to an Event object.
     * 
     * @param rs The ResultSet from which to extract event data.
     * @return An Event object populated with data from the ResultSet.
     * @throws SQLException If there is an error accessing the ResultSet.
     */
    private Event mapResultSetToEvent(ResultSet rs) throws SQLException {
        Date eventDate = null;
        String errorData = rs.getString("error_data");

        try {
            // Try to get actual date; fallback to event_date if no parsed date is available
            eventDate = rs.getDate("actual_date") != null ? rs.getDate("actual_date") : rs.getDate("event_date");
        } catch (SQLException e) {
            System.err.println("Error parsing date for event ID " + rs.getInt("id") + ": " + e.getMessage());
        }

        return new Event(
                rs.getInt("id"),
                rs.getString("event_name"),
                rs.getString("event_description"),
                eventDate,
                rs.getString("location"),
                rs.getString("college"),
                rs.getInt("created_by"),
                rs.getString("event_poster"),
                rs.getString("approval_status"),
                errorData
        );
    }
}
