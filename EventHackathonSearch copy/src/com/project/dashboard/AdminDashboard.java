package com.project.dashboard;

import com.project.models.Event;
import com.project.models.User;
import com.project.services.EventService;
import com.project.services.UserService;
import com.project.auth.LoginPage;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class AdminDashboard extends JFrame {
    private EventService eventService = new EventService();
    private UserService userService = new UserService();
    private JTable eventTable;
    private JTable organizerTable;
    private DefaultTableModel eventTableModel;
    private DefaultTableModel organizerTableModel;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(900, 600);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Header panel with logout button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(34, 49, 63));
        JLabel headerLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerLabel.setForeground(new Color(255, 215, 0)); // Gold color
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        // Logout button
        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton, new Color(231, 76, 60), Color.BLACK);
        logoutButton.setPreferredSize(new Dimension(100, 40));
        logoutButton.addActionListener(e -> logout());
        headerPanel.add(logoutButton, BorderLayout.EAST);

        // Event management panel
        JPanel eventPanel = new JPanel(new BorderLayout());
        eventPanel.setBorder(BorderFactory.createTitledBorder("Event Management"));
        eventPanel.setBackground(new Color(44, 62, 80));
        eventTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Date", "Location", "College"}, 0);
        eventTable = new JTable(eventTableModel);
        JScrollPane eventScrollPane = new JScrollPane(eventTable);
        eventPanel.add(eventScrollPane, BorderLayout.CENTER);

        JPanel eventButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        eventButtonPanel.setBackground(new Color(44, 62, 80));
        JButton addEventButton = new JButton("Add Event");
        styleButton(addEventButton, new Color(46, 204, 113), Color.BLACK);
        addEventButton.addActionListener(new AddEventListener());

        JButton deleteEventButton = new JButton("Delete Event");
        styleButton(deleteEventButton, new Color(231, 76, 60), Color.BLACK);
        deleteEventButton.addActionListener(new DeleteEventListener());

        eventButtonPanel.add(addEventButton);
        eventButtonPanel.add(deleteEventButton);
        eventPanel.add(eventButtonPanel, BorderLayout.SOUTH);

        // Organizer approval panel
        JPanel organizerPanel = new JPanel(new BorderLayout());
        organizerPanel.setBorder(BorderFactory.createTitledBorder("Pending Organizer Approvals"));
        organizerPanel.setBackground(new Color(44, 62, 80));
        organizerTableModel = new DefaultTableModel(new String[]{"ID", "Username", "College", "Email"}, 0);
        organizerTable = new JTable(organizerTableModel);
        JScrollPane organizerScrollPane = new JScrollPane(organizerTable);
        organizerPanel.add(organizerScrollPane, BorderLayout.CENTER);

        JPanel organizerButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        organizerButtonPanel.setBackground(new Color(44, 62, 80));
        JButton approveButton = new JButton("Approve");
        styleButton(approveButton, new Color(46, 204, 113), Color.BLACK);
        approveButton.addActionListener(new ApproveOrganizerListener());

        JButton rejectButton = new JButton("Reject");
        styleButton(rejectButton, new Color(231, 76, 60), Color.BLACK);
        rejectButton.addActionListener(new RejectOrganizerListener());

        organizerButtonPanel.add(approveButton);
        organizerButtonPanel.add(rejectButton);
        organizerPanel.add(organizerButtonPanel, BorderLayout.SOUTH);

        // Adding panels to the main layout
        add(headerPanel, BorderLayout.NORTH);
        add(eventPanel, BorderLayout.CENTER);
        add(organizerPanel, BorderLayout.SOUTH);

        loadEvents();
        loadPendingOrganizers();
    }

    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(120, 40));
    }

    // Load events into the event table
    private void loadEvents() {
        eventTableModel.setRowCount(0); // Clear existing rows
        List<Event> events = eventService.getAllEvents();
        for (Event event : events) {
            eventTableModel.addRow(new Object[]{event.getId(), event.getName(), event.getDate(), event.getLocation(), event.getCollege()});
        }
    }

    // Load pending organizers into the organizer table
    private void loadPendingOrganizers() {
        organizerTableModel.setRowCount(0); // Clear existing rows
        List<User> organizers = userService.getPendingOrganizers();
        for (User organizer : organizers) {
            organizerTableModel.addRow(new Object[]{organizer.getId(), organizer.getUsername(), organizer.getCollege(), organizer.getEmail()});
        }
    }

    // Logout action to close the dashboard and navigate back to login page
    private void logout() {
        dispose(); // Closes the current window
        new LoginPage().setVisible(true); // Opens the login page
        System.out.println("Admin logged out successfully.");
    }

    // Event listeners for event management
    private class AddEventListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            new AddEventPage().setVisible(true); // Opens the AddEventPage for adding event details
        }
    }

    private class DeleteEventListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = eventTable.getSelectedRow();
            if (selectedRow != -1) {
                int eventId = (int) eventTableModel.getValueAt(selectedRow, 0);
                if (eventService.deleteEvent(eventId)) {
                    loadEvents(); // Refresh event list
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Event deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Failed to delete event.");
                }
            } else {
                JOptionPane.showMessageDialog(AdminDashboard.this, "Please select an event to delete.");
            }
        }
    }

    // Event listeners for organizer approval
    private class ApproveOrganizerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = organizerTable.getSelectedRow();
            if (selectedRow != -1) {
                int organizerId = (int) organizerTableModel.getValueAt(selectedRow, 0);
                if (userService.updateOrganizerApprovalStatus(organizerId, "approved")) {
                    loadPendingOrganizers(); // Refresh organizer list
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Organizer approved successfully.");
                } else {
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Failed to approve organizer.");
                }
            } else {
                JOptionPane.showMessageDialog(AdminDashboard.this, "Please select an organizer to approve.");
            }
        }
    }

    private class RejectOrganizerListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = organizerTable.getSelectedRow();
            if (selectedRow != -1) {
                int organizerId = (int) organizerTableModel.getValueAt(selectedRow, 0);
                if (userService.updateOrganizerApprovalStatus(organizerId, "rejected")) {
                    loadPendingOrganizers(); // Refresh organizer list
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Organizer rejected successfully.");
                } else {
                    JOptionPane.showMessageDialog(AdminDashboard.this, "Failed to reject organizer.");
                }
            } else {
                JOptionPane.showMessageDialog(AdminDashboard.this, "Please select an organizer to reject.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminDashboard().setVisible(true));
    }
}
