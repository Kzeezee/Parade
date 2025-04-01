package com.g2t2.app;

import com.g2t2.db.DBController;
import com.g2t2.game.*;
import com.g2t2.util.Utility;

/**
 * Entry point for the G2T2 Parade application.
 * This class contains the main method for the console-based version of the
 * application.
 * 
 * If GUI functionality is implemented in the future, the commented-out JavaFX
 * code can be used as a starting point.
 * 
 */
public class App {
    /**
     * Main method for launching the console-based application.
     * It clears the console screen, displays a welcome message, initializes the database, and starts the main menu.
     * 
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        Utility.clearConsoleScreen();
        System.out.println("Hello world! This is G2T2 Parade!");
        DBController.init();
        StartMenu.showStartMenu();
    }
}