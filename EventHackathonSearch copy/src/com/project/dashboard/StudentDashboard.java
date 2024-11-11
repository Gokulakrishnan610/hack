package com.project.dashboard;

import com.project.models.Event;
import com.project.services.EventService;
import com.project.services.RegistrationService;
import com.project.auth.LoginPage; // Import your LoginPage class

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class StudentDashboard extends JFrame {
    private EventService eventService = new EventService();
    private RegistrationService registrationService = new RegistrationService();
    private JTable inCampusEventTable;
    private JTable otherCollegeEventTable;
    private JTable approvedEventTable;
    private DefaultTableModel inCampusTableModel;
    private DefaultTableModel otherCollegeTableModel;
    private DefaultTableModel approvedTableModel;
    private int studentId;
    private String studentCollege;

    public StudentDashboard(int studentId, String college) {
        this.studentId = studentId;
        this.studentCollege = college;

        setTitle("Student Dashboard");
        setSize(1000, 700);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Header panel with logout button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(34, 49, 63));

        JLabel headerLabel = new JLabel("Student Dashboard - Event Registration", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerLabel.setForeground(new Color(255, 215, 0)); // Gold color
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        // Logout button
        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton, new Color(231, 76, 60), Color.BLACK);
        logoutButton.setPreferredSize(new Dimension(100, 40));
        logoutButton.addActionListener(e -> logout());
        headerPanel.add(logoutButton, BorderLayout.EAST);

        // Main content panel with GridLayout to organize panels
        JPanel contentPanel = new JPanel(new GridLayout(1, 3, 0, 0));

        // Initialize tables and models for events
        inCampusTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Date", "Location"}, 0);
        otherCollegeTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Date", "Location"}, 0);
        approvedTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Date", "Location"}, 0);

        inCampusEventTable = new JTable(inCampusTableModel);
        otherCollegeEventTable = new JTable(otherCollegeTableModel);
        approvedEventTable = new JTable(approvedTableModel);

        // Panels for each type of events
        JPanel inCampusPanel = createEventPanel("In-Campus Events", inCampusEventTable);
        JPanel otherCollegePanel = createEventPanel("Other College Events", otherCollegeEventTable);
        JPanel approvedEventsPanel = createEventPanel("Approved Events", approvedEventTable);

        // Add register/unregister button panels
        inCampusPanel.add(createButtonPanel(inCampusEventTable, true), BorderLayout.SOUTH);
        otherCollegePanel.add(createButtonPanel(otherCollegeEventTable, false), BorderLayout.SOUTH);

        // Add event panels to content panel
        contentPanel.add(inCampusPanel);
        contentPanel.add(otherCollegePanel);
        contentPanel.add(approvedEventsPanel);

        // Add header and content panels to main layout
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        loadInCampusEvents();
        loadOtherCollegeEvents();
        loadApprovedEvents();
    }

    // Method to create and style event panel
    private JPanel createEventPanel(String title, JTable table) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setBackground(new Color(44, 62, 80));
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // Method to create button panel for register/unregister
    private JPanel createButtonPanel(JTable table, boolean allowUnregister) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(44, 62, 80));

        JButton registerButton = new JButton("Register");
        styleButton(registerButton, new Color(46, 204, 113), Color.BLACK);
        registerButton.addActionListener(new RegisterListener(table));
        buttonPanel.add(registerButton);

        if (allowUnregister) {
            JButton unregisterButton = new JButton("Unregister");
            styleButton(unregisterButton, new Color(231, 76, 60), Color.BLACK);
            unregisterButton.addActionListener(new UnregisterListener(table));
            buttonPanel.add(unregisterButton);
        }

        return buttonPanel;
    }

    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(120, 40));
    }

    // Load in-campus events specific to the student's college
    private void loadInCampusEvents() {
        inCampusTableModel.setRowCount(0); // Clear existing rows
        List<Event> events = eventService.getEventsByCollege(studentCollege);
        for (Event event : events) {
            inCampusTableModel.addRow(new Object[]{event.getId(), event.getName(), event.getDate(), event.getLocation()});
        }
    }

    // Load events from other colleges
    private void loadOtherCollegeEvents() {
        otherCollegeTableModel.setRowCount(0); // Clear existing rows
        List<Event> events = eventService.getAllEvents();
        for (Event event : events) {
            if (!event.getCollege().equals(studentCollege)) {
                otherCollegeTableModel.addRow(new Object[]{event.getId(), event.getName(), event.getDate(), event.getLocation()});
            }
        }
    }

    // Load approved events specific to the student
    private void loadApprovedEvents() {
        approvedTableModel.setRowCount(0); // Clear existing rows
        List<Event> events = registrationService.getApprovedEventsByUser(studentId);
        for (Event event : events) {
            approvedTableModel.addRow(new Object[]{event.getId(), event.getName(), event.getDate(), event.getLocation()});
        }
    }

    // Logout action to close the dashboard and navigate back to login page
    private void logout() {
        dispose(); // Closes the current window
        new LoginPage().setVisible(true); // Opens the login page
        System.out.println("User logged out successfully.");
    }

    // Event listener for registering to an event
    private class RegisterListener implements ActionListener {
        private JTable targetTable;

        public RegisterListener(JTable targetTable) {
            this.targetTable = targetTable;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = targetTable.getSelectedRow();
            if (selectedRow != -1) {
                int eventId = (int) targetTable.getValueAt(selectedRow, 0);

                // Check if already registered for the event
                if (registrationService.isAlreadyRegistered(studentId, eventId)) {
                    JOptionPane.showMessageDialog(StudentDashboard.this, "You are already registered for this event.");
                } else {
                    boolean success = registrationService.registerForEvent(studentId, eventId);
                    if (success) {
                        JOptionPane.showMessageDialog(StudentDashboard.this, "Registration request sent. Awaiting approval.");
                    } else {
                        JOptionPane.showMessageDialog(StudentDashboard.this, "Registration failed. Please try again.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(StudentDashboard.this, "Please select an event to register.");
            }
        }
    }

    // Event listener for unregistering from an event
    private class UnregisterListener implements ActionListener {
        private JTable targetTable;

        public UnregisterListener(JTable targetTable) {
            this.targetTable = targetTable;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = targetTable.getSelectedRow();
            if (selectedRow != -1) {
                int eventId = (int) targetTable.getValueAt(selectedRow, 0);
                boolean success = registrationService.unregisterFromEvent(studentId, eventId);
                if (success) {
                    JOptionPane.showMessageDialog(StudentDashboard.this, "Successfully unregistered from the event.");
                    loadApprovedEvents(); // Refresh approved events after unregistration
                } else {
                    JOptionPane.showMessageDialog(StudentDashboard.this, "Unregistration failed.");
                }
            } else {
                JOptionPane.showMessageDialog(StudentDashboard.this, "Please select an event to unregister.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentDashboard(2, "Rajalakshmi Engineering College").setVisible(true)); // Example student ID and college
    }
}
