package com.project.dashboard;

import com.project.utils.DBConnection;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.*;

public class AddEventPage extends JFrame {

    private JTextField eventNameField;
    private JTextField eventLocationField;
    private JTextField eventDateField;
    private JTextField collegeField;
    private JTextField posterField;
    private JTextArea eventDescriptionField;
    private JTextField createdByField;

    public AddEventPage() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Add New Event");
        setSize(500, 600);
        setMinimumSize(new Dimension(400, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(44, 62, 80));

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(34, 49, 63));
        JLabel headerLabel = new JLabel("Add New Event", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerLabel.setForeground(new Color(255, 215, 0)); // Gold color for title text
        headerPanel.add(headerLabel);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(44, 62, 80));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add fields to the form
        addField(formPanel, gbc, "Event Name:", eventNameField = new JTextField(20), 0);
        addField(formPanel, gbc, "Event Location:", eventLocationField = new JTextField(20), 1);
        addField(formPanel, gbc, "Event Date (YYYY-MM-DD):", eventDateField = new JTextField(20), 2);
        addField(formPanel, gbc, "College:", collegeField = new JTextField(20), 3);
        addField(formPanel, gbc, "Poster URL:", posterField = new JTextField(20), 4);

        JLabel descriptionLabel = new JLabel("Event Description:");
        descriptionLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(descriptionLabel, gbc);

        eventDescriptionField = new JTextArea(3, 20);
        eventDescriptionField.setLineWrap(true);
        eventDescriptionField.setWrapStyleWord(true);
        gbc.gridx = 1;
        formPanel.add(new JScrollPane(eventDescriptionField), gbc);

        addField(formPanel, gbc, "Created By (ID):", createdByField = new JTextField(20), 6);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(44, 62, 80));

        JButton addButton = new JButton("Add Event");
        styleButton(addButton, new Color(39, 174, 96), Color.BLACK); // Green color
        addButton.addActionListener(new AddEventAction());
        buttonPanel.add(addButton);

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, new Color(231, 76, 60), Color.BLACK); // Red color
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(120, 40));
    }

    private void addField(JPanel formPanel, GridBagConstraints gbc, String labelText, JTextField textField, int gridy) {
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = gridy;
        formPanel.add(label, gbc);

        gbc.gridx = 1;
        formPanel.add(textField, gbc);
    }

    private class AddEventAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String eventName = eventNameField.getText().trim();
            String eventLocation = eventLocationField.getText().trim();
            String eventDate = eventDateField.getText().trim();
            String college = collegeField.getText().trim();
            String poster = posterField.getText().trim();
            String eventDescription = eventDescriptionField.getText().trim();
            String createdBy = createdByField.getText().trim();

            if (eventName.isEmpty() || eventLocation.isEmpty() || eventDate.isEmpty() ||
                college.isEmpty() || eventDescription.isEmpty() || createdBy.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill in all fields.");
                return;
            }

            try {
                LocalDate.parse(eventDate);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(null, "Invalid date format. Please use YYYY-MM-DD.");
                return;
            }

            if (addEvent(eventName, eventLocation, eventDate, college, poster, eventDescription, createdBy)) {
                JOptionPane.showMessageDialog(null, "Event added successfully!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Error adding event.");
            }
        }

        private boolean addEvent(String eventName, String eventLocation, String eventDate, String college,
                                 String poster, String eventDescription, String createdBy) {
            String query = "INSERT INTO events (event_name, location, event_date, college, event_poster, event_description, created_by) " +
                           "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, eventName);
                stmt.setString(2, eventLocation);
                stmt.setDate(3, Date.valueOf(eventDate));
                stmt.setString(4, college);
                stmt.setString(5, poster);
                stmt.setString(6, eventDescription);
                stmt.setInt(7, Integer.parseInt(createdBy)); // Parse to integer

                int rowsInserted = stmt.executeUpdate();
                return rowsInserted > 0;
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
                ex.printStackTrace();
                return false;
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Created By field must be a valid numeric ID.");
                return false;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AddEventPage().setVisible(true));
    }
}
