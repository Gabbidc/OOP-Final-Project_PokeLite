package com.pokelite;

import javax.swing.SwingUtilities;
import com.pokelite.GameWindow;

public class main {
    public static void main(String[] args) {
        // Run the GUI on the Event Dispatch Thread (Best Practice)
        SwingUtilities.invokeLater(() -> {
            GameWindow game = new GameWindow();
            game.setVisible(true);
        });
    }
}