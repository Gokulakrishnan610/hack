package com.project.auth;

import com.project.utils.DBConnection;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.*;

public class RegisterPage extends JFrame {
    private JTextField nameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private JTextField collegeField;
    private JTextField emailField;

    public RegisterPage() {
        setTitle("EventConnect - Register");
        setSize(500, 500);
        setMinimumSize(new Dimension(400, 450));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(34, 49, 63));

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(34, 49, 63));
        JLabel headerLabel = new JLabel("EventConnect Registration", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerLabel.setForeground(new Color(255, 215, 0));
        headerPanel.add(headerLabel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(44, 62, 80));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name Field
        addField(formPanel, gbc, "Name:", nameField = new JTextField(20), 0);

        // Username Field
        addField(formPanel, gbc, "Username:", usernameField = new JTextField(20), 1);

        // Password Field
        addField(formPanel, gbc, "Password:", passwordField = new JPasswordField(20), 2);

        // Role Dropdown
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(roleLabel, gbc);

        String[] roles = {"student", "admin", "organizer"};
        roleCombo = new JComboBox<>(roles);
        gbc.gridx = 1;
        formPanel.add(roleCombo, gbc);

        // College Field
        addField(formPanel, gbc, "College:", collegeField = new JTextField(20), 4);

        // Email Field
        addField(formPanel, gbc, "Email:", emailField = new JTextField(20), 5);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonsPanel.setBackground(new Color(34, 49, 63));

        JButton registerButton = new JButton("Register");
        styleButton(registerButton, new Color(93, 173, 226), Color.BLACK);
        registerButton.addActionListener(new RegisterAction());
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

    private void addField(JPanel formPanel, GridBagConstraints gbc, String labelText, JTextField textField, int gridy) {
        JLabel label = new JLabel(labelText);
        label.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = gridy;
        formPanel.add(label, gbc);

        gbc.gridx = 1;
        formPanel.add(textField, gbc);
    }

    private class RegisterAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText();
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = (String) roleCombo.getSelectedItem();
            String college = collegeField.getText();
            String email = emailField.getText();
            String approvalStatus = role.equals("organizer") ? "pending" : "approved";

            if (registerUser(name, username, password, role, college, email, approvalStatus)) {
                JOptionPane.showMessageDialog(null, "Registration successful!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(null, "Registration failed. Please try again.");
            }
        }

        private boolean registerUser(String name, String username, String password, String role, String college, String email, String approvalStatus) {
            String query = "INSERT INTO users (name, username, password, role, college, email, approval_status) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, name);
                stmt.setString(2, username);
                stmt.setString(3, password);
                stmt.setString(4, role);
                stmt.setString(5, college);
                stmt.setString(6, email);
                stmt.setString(7, approvalStatus);

                return stmt.executeUpdate() > 0;
            } catch (SQLException ex) {
                ex.printStackTrace();
                return false;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterPage().setVisible(true));
    }
}
