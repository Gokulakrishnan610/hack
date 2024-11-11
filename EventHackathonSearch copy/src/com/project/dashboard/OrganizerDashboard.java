package com.project.dashboard;

import com.project.models.Event;
import com.project.models.Registration;
import com.project.services.EventService;
import com.project.services.RegistrationService;
import com.project.auth.LoginPage;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class OrganizerDashboard extends JFrame {
    private EventService eventService = new EventService();
    private RegistrationService registrationService = new RegistrationService();
    private JTable eventTable;
    private JTable registrationTable;
    private DefaultTableModel eventTableModel;
    private DefaultTableModel registrationTableModel;
    private String organizerCollege;

    public OrganizerDashboard(String college) {
        this.organizerCollege = college;

        setTitle("Organizer Dashboard");
        setSize(900, 600);
        setMinimumSize(new Dimension(800, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel headerPanel = createHeaderPanel();
        JPanel eventPanel = createEventManagementPanel();
        JPanel registrationPanel = createRegistrationPanel();

        add(headerPanel, BorderLayout.NORTH);
        add(eventPanel, BorderLayout.CENTER);
        add(registrationPanel, BorderLayout.SOUTH);

        loadEvents();
        loadPendingRegistrations();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(34, 49, 63));
        JLabel headerLabel = new JLabel("Organizer Dashboard", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerLabel.setForeground(new Color(255, 215, 0));
        headerPanel.add(headerLabel, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton, new Color(231, 76, 60), Color.BLACK);
        logoutButton.setPreferredSize(new Dimension(100, 40));
        logoutButton.addActionListener(e -> logout());
        headerPanel.add(logoutButton, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createEventManagementPanel() {
        JPanel eventPanel = new JPanel(new BorderLayout());
        eventPanel.setBorder(BorderFactory.createTitledBorder("Event Management"));
        eventPanel.setBackground(new Color(44, 62, 80));

        eventTableModel = new DefaultTableModel(new String[]{"ID", "Name", "Date", "Location"}, 0);
        eventTable = new JTable(eventTableModel);
        JScrollPane eventScrollPane = new JScrollPane(eventTable);
        eventPanel.add(eventScrollPane, BorderLayout.CENTER);

        JPanel eventButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        eventButtonPanel.setBackground(new Color(44, 62, 80));

        JButton addEventButton = new JButton("Add Event");
        styleButton(addEventButton, new Color(46, 204, 113), Color.BLACK);
        addEventButton.addActionListener(e -> new AddEventPage().setVisible(true));

        JButton updateEventButton = new JButton("Update Event");
        styleButton(updateEventButton, new Color(241, 196, 15), Color.BLACK);
        updateEventButton.addActionListener(new UpdateEventListener());

        JButton deleteEventButton = new JButton("Delete Event");
        styleButton(deleteEventButton, new Color(231, 76, 60), Color.BLACK);
        deleteEventButton.addActionListener(new DeleteEventListener());

        eventButtonPanel.add(addEventButton);
        eventButtonPanel.add(updateEventButton);
        eventButtonPanel.add(deleteEventButton);
        eventPanel.add(eventButtonPanel, BorderLayout.SOUTH);

        return eventPanel;
    }

    private JPanel createRegistrationPanel() {
        JPanel registrationPanel = new JPanel(new BorderLayout());
        registrationPanel.setBorder(BorderFactory.createTitledBorder("Pending Student Registrations"));
        registrationPanel.setBackground(new Color(44, 62, 80));

        registrationTableModel = new DefaultTableModel(new String[]{"ID", "Student Name", "Student ID", "Event ID", "Status"}, 0);
        registrationTable = new JTable(registrationTableModel);
        JScrollPane registrationScrollPane = new JScrollPane(registrationTable);
        registrationPanel.add(registrationScrollPane, BorderLayout.CENTER);

        JPanel registrationButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        registrationButtonPanel.setBackground(new Color(44, 62, 80));

        JButton approveButton = new JButton("Approve");
        styleButton(approveButton, new Color(46, 204, 113), Color.BLACK);
        approveButton.addActionListener(new ApproveRegistrationListener());

        JButton rejectButton = new JButton("Reject");
        styleButton(rejectButton, new Color(231, 76, 60), Color.BLACK);
        rejectButton.addActionListener(new RejectRegistrationListener());

        registrationButtonPanel.add(approveButton);
        registrationButtonPanel.add(rejectButton);
        registrationPanel.add(registrationButtonPanel, BorderLayout.SOUTH);

        return registrationPanel;
    }

    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(120, 40));
    }

    private void loadEvents() {
        eventTableModel.setRowCount(0);
        List<Event> events = eventService.getEventsByCollege(organizerCollege);
        for (Event event : events) {
            eventTableModel.addRow(new Object[]{event.getId(), event.getName(), event.getDate(), event.getLocation()});
        }
    }

    private void loadPendingRegistrations() {
        registrationTableModel.setRowCount(0);
        List<Registration> registrations = registrationService.getPendingRegistrationsByCollege(organizerCollege);
        for (Registration registration : registrations) {
            registrationTableModel.addRow(new Object[]{
                registration.getId(),
                registration.getStudentName(),
                registration.getUserId(),
                registration.getEventId(),
                registration.getApprovalStatus()
            });
        }
    }

    private void logout() {
        dispose();
        new LoginPage().setVisible(true);
    }

    private class UpdateEventListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = eventTable.getSelectedRow();
            if (selectedRow != -1) {
                int eventId = (int) eventTableModel.getValueAt(selectedRow, 0);
                JOptionPane.showMessageDialog(OrganizerDashboard.this, "Update event dialog for event ID " + eventId);
            } else {
                JOptionPane.showMessageDialog(OrganizerDashboard.this, "Please select an event to update.");
            }
        }
    }

    private class DeleteEventListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = eventTable.getSelectedRow();
            if (selectedRow != -1) {
                int eventId = (int) eventTableModel.getValueAt(selectedRow, 0);
                if (eventService.deleteEvent(eventId)) {
                    loadEvents();
                    JOptionPane.showMessageDialog(OrganizerDashboard.this, "Event deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(OrganizerDashboard.this, "Failed to delete event.");
                }
            } else {
                JOptionPane.showMessageDialog(OrganizerDashboard.this, "Please select an event to delete.");
            }
        }
    }

    private class ApproveRegistrationListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = registrationTable.getSelectedRow();
            if (selectedRow != -1) {
                int registrationId = (int) registrationTableModel.getValueAt(selectedRow, 0);
                if (registrationService.updateRegistrationStatus(registrationId, "approved")) {
                    loadPendingRegistrations();
                    JOptionPane.showMessageDialog(OrganizerDashboard.this, "Registration approved successfully.");
                } else {
                    JOptionPane.showMessageDialog(OrganizerDashboard.this, "Failed to approve registration.");
                }
            } else {
                JOptionPane.showMessageDialog(OrganizerDashboard.this, "Please select a registration to approve.");
            }
        }
    }

    private class RejectRegistrationListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = registrationTable.getSelectedRow();
            if (selectedRow != -1) {
                int registrationId = (int) registrationTableModel.getValueAt(selectedRow, 0);
                if (registrationService.updateRegistrationStatus(registrationId, "rejected")) {
                    loadPendingRegistrations();
                    JOptionPane.showMessageDialog(OrganizerDashboard.this, "Registration rejected successfully.");
                } else {
                    JOptionPane.showMessageDialog(OrganizerDashboard.this, "Failed to reject registration.");
                }
            } else {
                JOptionPane.showMessageDialog(OrganizerDashboard.this, "Please select a registration to reject.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OrganizerDashboard("Rajalakshmi Engineering College").setVisible(true));
    }
}
