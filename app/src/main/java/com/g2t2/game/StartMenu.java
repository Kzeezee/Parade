package com.g2t2.game;

import java.util.*;

import com.g2t2.enums.CardColour;
import com.g2t2.types.ParadeBoard;
import com.g2t2.types.Player;
import com.g2t2.types.PlayerProfile;
import com.g2t2.util.*;
import com.g2t2.util.db.DBController;

/**
 * Class handling the main menu of the application
 * Has methods handling GUI version and Console version
 * 
 * @author Oliver
 */
public class StartMenu {

    /**
     * Main method to display the start menu of the application
     * 
     * @author Oliver
     */
    public static void showStartMenu() {
        if (StateFlags.isGUIMode) {
            GUIStartMenu();
        } else {
            if (StateManager.getPlayerProfile() == null) {
                checkForPlayerProfiles();
            } else {
                consoleStartMenu();
            }
        }
    }

    /**
     * Let player select which profile he/she to play on.
     * This allows the player to track various game progress.
     * 
     * @author Oliver
     */
    public static void selectPlayerProfile(List<PlayerProfile> players) {
        System.out.println(Constants.ANSI_RESET);
        System.out.println(Constants.BANNER);
        System.out.println(Constants.DIVIDER);
        Scanner sc = new Scanner(System.in);

        // If it isn't empty, display selection of all the profiles.
        for (int i = 0; i < players.size(); i++) {
            System.out.printf(" 📌 %s | %d wins | (%d)\n", players.get(i).getName(), players.get(i).getWins(),
                    i + 1);
        }
        System.out.println("\n\n\n");

        System.out.println(" 📌 Select your Parade profile (" + 1 + "-" + players.size() + ")");
        boolean validOptionSelected = false;
        while (!validOptionSelected) {
            try {
                System.out.print(Display.colorString(" ⚜ Enter option ➢ "));
                String s = sc.nextLine();
                System.out.print(Constants.ANSI_RESET);
                int option = Integer.parseInt(s);
                if (option > players.size() || option <= 0) {
                    throw new NumberFormatException("No such player profile");
                }

                validOptionSelected = true;
                PlayerProfile pp = players.get(option - 1);
                // Set StateManager's current player profile
                StateManager.setPlayerProfile(pp);
            } catch (NumberFormatException e) {
                System.out.println(Display.errorColour(" ❌ Invalid input"));
            }
        }
    }

    /**
     * Display a start menu for the console application
     * 
     * @author Oliver
     */
    private static void consoleStartMenu() {
        // Utility.clearConsoleScreen();
        Scanner sc = new Scanner(System.in);
        int optionChosen = 0;

        boolean validOptionSelected = false;
        while (!validOptionSelected) {
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
                    case 2:
                        // Start lobby
                        validOptionSelected = true;
                        OnlineLobby.displayLobby();
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

    private static void checkForPlayerProfiles() {
        System.out.println(Constants.ANSI_RESET);
        System.out.println(Constants.BANNER);
        System.out.println(Constants.DIVIDER);
        System.out.println("Loading in progress...");
        System.out.println(Constants.ANSI_RESET);

        // DB search what profiles exist
        List<PlayerProfile> players = DBController.getPlayerProfiles();

        // Show Player Profile Creation if empty
        if (players.isEmpty()) {
            showPlayerProfileCreation();
        } else {
            selectPlayerProfile(players);
        }
    }

    private static void showPlayerProfileCreation() {
        System.out.println(Constants.ANSI_RESET);
        System.out.println(Constants.BANNER);
        System.out.println(Constants.DIVIDER);
        Scanner sc = new Scanner(System.in);

        Utility.clearConsoleScreen();
        // Prompt user to create new user profile
        System.out.println(Display.colorString("There are no profiles detected. Create a new profile?"));
        String name = "";
        while (true) {
            try {
                System.out.print(Display.colorString(" ➢ Enter your name: "));
                name = sc.nextLine();
                if (name.isEmpty()) {
                    throw new InputMismatchException(" 📌 Please enter an appropriate name.");
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println(e.getMessage());
                System.out.println();
            }
        }
        System.out.println(Display.colorString("Creating player profile: " + name));
        System.out.println(Constants.ANSI_RESET);

        // Create new value in DB
        if (DBController.createPlayerProfile(name) == true) {
            selectPlayerProfile(DBController.getPlayerProfiles());
        }
    }

    /**
     * Display a start menu for the GUI application.
     * Currently empty.
     * 
     * @author Oliver
     */
    private static void GUIStartMenu() {

    }
}
