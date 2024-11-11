package com.project.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/sample";
    private static final String USER = "root";
    private static final String PASSWORD = ""; 

    static {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error loading JDBC Driver: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        // Establish the connection to the database
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
