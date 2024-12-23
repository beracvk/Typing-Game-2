package TypingGame;

import javax.swing.*;

public class TypingGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Typing Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            MainMenu mainMenu = new MainMenu(frame);
            frame.add(mainMenu);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setUndecorated(true); 
            frame.setVisible(true);
        });
    }
}
