package com.project;

import com.project.api.FetchMLHEvents;
import com.project.auth.LoginPage;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

public class EventConnect {

    public static void main(String[] args) {
        // Display the splash screen and begin background event fetching
        SplashScreen splash = new SplashScreen();
        splash.setVisible(true);

        // Start fetching MLH events asynchronously
        new Thread(FetchMLHEvents::startFetching).start();

        // Schedule transition from splash screen to login page
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                splash.dispose(); // Close splash screen
                SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true)); // Open LoginPage
            }
        }, 7000); // Splash screen duration set to 7 seconds
    }
}

class SplashScreen extends JFrame {
    private JLabel animatedTitleLabel;
    private JLabel subtitleLabel;
    private JProgressBar progressBar;

    public SplashScreen() {
        setTitle("Welcome to Hackfolio");
        setSize(600, 400);
        setLocationRelativeTo(null); // Center the window
        setUndecorated(true); // Remove title bar for a streamlined look

        JPanel panel = createMainPanel();
        add(panel); // Add main panel to the frame

        // Animate title with a callback to show other elements upon completion
        animateTitle("Hackfolio", () -> {
            subtitleLabel.setVisible(true);
            progressBar.setVisible(true);
        });
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(44, 62, 80));
        mainPanel.setBorder(BorderFactory.createLineBorder(new Color(46, 204, 113), 3, true)); // Rounded border

        JPanel centerPanel = createCenterPanel();
        JPanel bottomPanel = createBottomPanel();

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(44, 62, 80));
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        animatedTitleLabel = new JLabel("", SwingConstants.CENTER);
        animatedTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        animatedTitleLabel.setForeground(new Color(255, 223, 0)); // Gold for title text
        animatedTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        subtitleLabel = new JLabel("Connecting You to Opportunities", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.ITALIC, 18));
        subtitleLabel.setForeground(new Color(189, 195, 199));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setVisible(false);

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(animatedTitleLabel);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space between title and subtitle
        centerPanel.add(subtitleLabel);
        centerPanel.add(Box.createVerticalGlue());

        return centerPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(44, 62, 80));

        JLabel loadingLabel = new JLabel("Loading, please wait...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        loadingLabel.setForeground(new Color(189, 195, 199));
        
        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setBackground(new Color(52, 73, 94));
        progressBar.setForeground(new Color(39, 174, 96));
        progressBar.setPreferredSize(new Dimension(580, 20));
        progressBar.setVisible(false);

        bottomPanel.add(loadingLabel, BorderLayout.NORTH);
        bottomPanel.add(progressBar, BorderLayout.SOUTH);

        return bottomPanel;
    }

    /**
     * Animates the title text letter by letter.
     *
     * @param text The text to animate.
     * @param onComplete Runnable to run after animation completion.
     */
    private void animateTitle(String text, Runnable onComplete) {
        Timer timer = new Timer();
        StringBuilder displayedText = new StringBuilder();
        int delay = 500; // Delay for each letter

        for (int i = 0; i < text.length(); i++) {
            int index = i;
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    displayedText.append(text.charAt(index));
                    animatedTitleLabel.setText(displayedText.toString());
                    if (index == text.length() - 1) {
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                onComplete.run(); // Display subtitle and progress bar when complete
                            }
                        }, 500);
                    }
                }
            }, delay * (i + 1));
        }
    }
}
