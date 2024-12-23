package TypingGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MainMenu extends JPanel {
    private final JFrame parentFrame;
    private boolean isFullScreen = true;

    public MainMenu(JFrame frame) {
        this.parentFrame = frame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.BLACK);

        JLabel titleLabel = new JLabel("Typing Game", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.PLAIN, 24));
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(e -> startGame());

        JButton optionsButton = new JButton("Options");
        optionsButton.setFont(new Font("Arial", Font.PLAIN, 24));
        optionsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        optionsButton.addActionListener(e -> showOptions());

        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 24));
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.addActionListener(e -> System.exit(0));

        add(Box.createVerticalGlue());
        add(titleLabel);
        add(Box.createRigidArea(new Dimension(0, 50)));
        add(startButton);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(optionsButton);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(exitButton);
        add(Box.createVerticalGlue());
    }

    private void startGame() {
        parentFrame.getContentPane().removeAll();
        GamePanel gamePanel = new GamePanel();
        
        parentFrame.add(gamePanel);
        parentFrame.revalidate();
        parentFrame.repaint();

        gamePanel.requestFocusInWindow();
        gamePanel.start();
    }

    private void showOptions() {
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
        optionsPanel.setBackground(Color.BLACK);

        JCheckBox fullscreenToggle = new JCheckBox("Fullscreen", isFullScreen);
        fullscreenToggle.setFont(new Font("Arial", Font.PLAIN, 24));
        fullscreenToggle.setBackground(Color.BLACK);
        fullscreenToggle.setForeground(Color.WHITE);
        fullscreenToggle.addActionListener(e -> toggleFullScreen(fullscreenToggle.isSelected()));

        optionsPanel.add(Box.createVerticalGlue());
        optionsPanel.add(fullscreenToggle);
        optionsPanel.add(Box.createVerticalGlue());

        int result = JOptionPane.showConfirmDialog(
            this,
            optionsPanel,
            "Options",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            toggleFullScreen(fullscreenToggle.isSelected());
        }
    }

    private void toggleFullScreen(boolean enableFullscreen) {
        isFullScreen = enableFullscreen;
        parentFrame.dispose();
        parentFrame.setUndecorated(isFullScreen);
        if (isFullScreen) {
            parentFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            parentFrame.setSize(1000, 800);
            parentFrame.setLocationRelativeTo(null);
        }
        parentFrame.setVisible(true);
    }
}
