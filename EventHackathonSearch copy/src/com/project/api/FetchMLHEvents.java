package com.project.api;

import com.project.utils.DBConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.json.JSONArray;
import org.json.JSONObject;

public class FetchMLHEvents {

    private static final String DEVPOST_API_URL = "https://devpost.com/api/hackathons";
    private static final int ADMIN_USER_ID = 1;

    public static void startFetching() {
        JSONArray events = fetchDevpostEvents();
        if (events != null) {
            insertEventsToDB(events);
        } else {
            System.out.println("No events found to insert.");
        }
    }

    private static JSONArray fetchDevpostEvents() {
        try {
            URL url = new URI(DEVPOST_API_URL + "?status=upcoming&page=1").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder content = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();

                JSONObject jsonResponse = new JSONObject(content.toString());
                return jsonResponse.getJSONArray("hackathons");

            } else {
                System.out.println("Failed to fetch data from Devpost API, Response code: " + responseCode);
            }
        } catch (Exception e) {
            System.err.println("Error fetching Devpost events: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private static void insertEventsToDB(JSONArray eventData) {
        try (Connection connection = DBConnection.getConnection()) {
            String insertQuery = "INSERT INTO events (event_name, event_description, event_date, location, college, created_by, event_poster, error_data) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            String selectQuery = "SELECT COUNT(*) FROM events WHERE event_name = ? AND (event_date = ? OR (event_date IS NULL AND error_data = ?))";

            for (int i = 0; i < eventData.length(); i++) {
                JSONObject event = eventData.getJSONObject(i);

                String eventName = event.optString("title", "Hackathon Event");

                // Get themes as a comma-separated string for the description
                JSONArray themesArray = event.optJSONArray("themes");
                StringBuilder eventDescription = new StringBuilder("Themes: ");
                if (themesArray != null) {
                    for (int j = 0; j < themesArray.length(); j++) {
                        eventDescription.append(themesArray.getJSONObject(j).optString("name", "General"));
                        if (j < themesArray.length() - 1) eventDescription.append(", ");
                    }
                } else {
                    eventDescription.append("No themes available");
                }

                String eventLocation = event.getJSONObject("displayed_location").optString("location", "Online");
                String eventUrl = event.optString("url", "");
                String startDateString = event.optString("submission_period_dates").split(" - ")[0].trim();

                Date eventDate = parseEventDate(startDateString);
                String errorData = null;

                // Set error data if date parsing fails
                if (eventDate == null) {
                    errorData = startDateString;
                    System.out.println("Invalid date format for event: " + startDateString);
                }

                // Check if event already exists in the database
                try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
                    selectStmt.setString(1, eventName);
                    selectStmt.setDate(2, eventDate);
                    selectStmt.setString(3, errorData);

                    try (ResultSet rs = selectStmt.executeQuery()) {
                        rs.next();
                        if (rs.getInt(1) > 0) {
                            System.out.println("Event already exists in database: " + eventName);
                            continue; // Skip to the next event if already exists
                        }
                    }
                }

                // Insert new event into the database
                try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                    insertStmt.setString(1, eventName);
                    insertStmt.setString(2, eventDescription.toString());
                    insertStmt.setDate(3, eventDate);  // Can be null if date parsing fails
                    insertStmt.setString(4, eventLocation);
                    insertStmt.setString(5, "Online");
                    insertStmt.setInt(6, ADMIN_USER_ID);
                    insertStmt.setString(7, eventUrl);
                    insertStmt.setString(8, errorData); // Insert only the date part if date is invalid

                    insertStmt.executeUpdate();
                    System.out.println("Inserted event: " + eventName);
                } catch (SQLException ex) {
                    System.out.println("Failed to insert event: " + eventName);
                    ex.printStackTrace();
                }
            }
            System.out.println("Events have been added successfully.");

        } catch (SQLException e) {
            System.out.println("Database connection error during event insertion.");
            e.printStackTrace();
        }
    }

    // Helper method to parse the date, returning null if parsing fails
    private static Date parseEventDate(String dateString) {
        String[] dateFormats = {"yyyy-MM-dd", "MMM dd, yyyy", "yyyy-MM-dd'T'HH:mm:ss", "MMM dd - dd, yyyy"};
        for (String format : dateFormats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                java.util.Date parsedDate = sdf.parse(dateString);
                return new Date(parsedDate.getTime());
            } catch (ParseException e) {
                // Continue to next format if parsing fails
            }
        }
        return null;
    }
}
