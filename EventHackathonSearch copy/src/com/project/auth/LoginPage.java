package com.project.auth;

import com.project.dashboard.AdminDashboard;
import com.project.dashboard.OrganizerDashboard;
import com.project.dashboard.StudentDashboard;
import com.project.utils.DBConnection;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;

    public LoginPage() {
        setTitle("Hackfolio - Login");
        setSize(500, 400);
        setMinimumSize(new Dimension(400, 300));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(34, 49, 63));

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(34, 49, 63));
        JLabel headerLabel = new JLabel("Hackfolio Login", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        headerLabel.setForeground(new Color(255, 215, 0));
        headerPanel.add(headerLabel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(44, 62, 80));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1;
        formPanel.add(usernameField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);

        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(roleLabel, gbc);

        String[] roles = {"student", "admin", "organizer"};
        roleCombo = new JComboBox<>(roles);
        gbc.gridx = 1;
        formPanel.add(roleCombo, gbc);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonsPanel.setBackground(new Color(34, 49, 63));

        JButton loginButton = new JButton("Login");
        styleButton(loginButton, new Color(72, 201, 176), Color.BLACK);
        loginButton.addActionListener(new LoginAction());
        buttonsPanel.add(loginButton);

        JButton registerButton = new JButton("Register");
        styleButton(registerButton, new Color(93, 173, 226), Color.BLACK);
        registerButton.addActionListener(e -> new RegisterPage().setVisible(true));
        buttonsPanel.add(registerButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(120, 40));
    }

    private class LoginAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String role = (String) roleCombo.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Please fill in all fields.");
                return;
            }

            if (authenticateUser(username, password, role)) {
                JOptionPane.showMessageDialog(null, "Login successful as " + role);

                switch (role) {
                    case "student":
                        int studentId = getUserId(username);
                        String college = getUserCollege(username);
                        new StudentDashboard(studentId, college).setVisible(true);
                        break;
                    case "admin":
                        new AdminDashboard().setVisible(true);
                        break;
                    case "organizer":
                        String organizerCollege = getUserCollege(username);
                        new OrganizerDashboard(organizerCollege).setVisible(true);
                        break;
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Invalid credentials. Please try again.");
                usernameField.setText(""); // Clear username field
                passwordField.setText(""); // Clear password field
            }
        }

        private boolean authenticateUser(String username, String password, String role) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ? AND role = ? AND approval_status = 'approved'";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password); // This should match the hashed password mechanism if hashing is used
                stmt.setString(3, role);

                ResultSet rs = stmt.executeQuery();
                return rs.next(); // Returns true if user exists
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
                ex.printStackTrace();
                return false;
            }
        }

        private int getUserId(String username) {
            String query = "SELECT id FROM users WHERE username = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
                ex.printStackTrace();
            }
            return -1; // Return -1 if user not found
        }

        private String getUserCollege(String username) {
            String query = "SELECT college FROM users WHERE username = ?";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getString("college");
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Database error: " + ex.getMessage());
                ex.printStackTrace();
            }
            return null; // Return null if college is not found
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}
