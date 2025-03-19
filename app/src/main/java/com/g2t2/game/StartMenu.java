package com.g2t2.game;

import java.util.Scanner;

import com.g2t2.enums.CardColour;
import com.g2t2.util.*;

/**
 * Class handling the main menu of the application
 * Has methods handling GUI version and Console version
 * @author Oliver
 */
public class StartMenu {

    /**
     * Main method to display the start menu of the application
     * @author Oliver
     */
    public static void showStartMenu() {
        if (StateFlags.isGUIMode) {
            GUIStartMenu();
        } else {
            consoleStartMenu();
        }
    }

    /**
     * Display a start menu for the console application
     * @author Oliver
     */
    private static void consoleStartMenu() {
        Utility.clearConsoleScreen();
        Scanner sc = new Scanner(System.in);
        int optionChosen = 0;

        boolean validOptionSelected = false;
        while (!validOptionSelected) {
            // TODO: Add library to format color codes to console
            System.out.println(Constants.ANSI_RESET);
            System.out.println(Constants.BANNER);
            System.out.println(Constants.DIVIDER);
            System.out.println(Constants.WELCOMEMESSAGE_STRING);
            System.out.println(" 📌 Play local (1)");
            System.out.println(" 📌 Play online (2)");
            System.out.println(" 📌 Exit application (3)");
            System.out.print(Display.colorString(" ⚜ Enter option ➢ "));
            String s = sc.nextLine();
            System.out.print(Constants.ANSI_RESET);
            try {
                optionChosen = Integer.parseInt(s);
                switch (optionChosen) {
                    case 1:
                        // Start options of game
                        validOptionSelected = true;
                        GameInit.displayGameOptions();
                        break;
                    case 3:
                        System.out.println(Constants.GOODBYE);
                        System.exit(0);
                    default:
                        throw new Exception(Display.errorColour(" ❌ Invalid input"));
                }
            } catch (NumberFormatException e) {
                Utility.clearConsoleScreen();
                System.out.println(Display.errorColour(Constants.GAME_SELECTION_OPTION_ERROR_MSG));
            } catch (Exception e) {
                Utility.clearConsoleScreen();
                System.out.println(Display.errorColour(Constants.GAME_SELECTION_OPTION_ERROR_MSG));
            }
        }
    }

    /**
     * Display a start menu for the GUI application.
     * Currently empty.
     * @author Oliver
     */
    private static void GUIStartMenu() {

    }
}
