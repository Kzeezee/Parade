package com.g2t2.game;

import com.g2t2.util.StateFlags;
import com.g2t2.util.Utility;

/**
 * Class handling all game initialization methods
 * @author Oliver
 */
public class GameInit {
    /**
     * Display the game options in either console or GUI
     * (depending on what isGUIMode in StateFlags is)
     * @author Oliver
     */
    public static void displayGameOptions() {
        if (StateFlags.isGUIMode) {
            // GUI display options
        } else {
            // Console configuration
            Utility.clearConsoleScreen();
            displayConsoleGameOptions();
        }
    }

    /**
     * Method displays the configurable game options in console
     * @author Oliver
     */
    private static void displayConsoleGameOptions() {
        // Allow user to configure players, difficulty?
        
    }
}
