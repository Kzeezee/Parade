package com.g2t2.game;

import java.util.Scanner;
import java.util.List;
import java.util.InputMismatchException;

import com.g2t2.db.dao.PlayerProfileDao;
import com.g2t2.db.dao.impl.PlayerProfileDaoImpl;
import com.g2t2.enums.PlayerProfileOptionInput;
import com.g2t2.types.PlayerProfile;
import com.g2t2.util.Constants;
import com.g2t2.util.Display;
import com.g2t2.util.Utility;

/**
 * Handles the main menu of the application.
 * Provides methods for both GUI and console-based menu interactions.
 * 
 * @author Oliver
 */
public class StartMenu {
    private static final String WELCOMEMESSAGE_STRING = " ⚜ Welcome to G2T2 Parade! Please select an option. (1, 3, 4 for now)";
    private static final String GAMEPLAY_OPTIONS = " 📌 Play local (1)\n" + " 📌 Play online (2)\n"
            + " 📌 Change Profile (3)\n"
            + " 📌 Exit application (4)\n"
            + Display.colorString(" ⚜ Enter option ➢ ");

    /**
     * Displays the start menu of the application based on the current mode (GUI or
     * console).
     */
    public static void showStartMenu() {
        if (StateManager.getPlayerProfile() == null) { // no host for the game is found
            checkForPlayerProfiles();
        } else {
            consoleStartMenu();
        }
    }

    /**
     * Displays the console-based start menu and handles user input.
     */
    private static void consoleStartMenu() {
        Utility.clearConsoleScreen();        
        promptForGamePlayOptions();
    }

    private static void promptForGamePlayOptions() {
        Scanner sc = new Scanner(System.in);
        Utility.clearConsoleScreen();
        while (true) {
            System.out.println(Constants.BANNER);
            System.out.println(Constants.DIVIDER);
            Display.delayPrint(WELCOMEMESSAGE_STRING, 20);
            System.out.print(GAMEPLAY_OPTIONS);
            String s = sc.nextLine();
            try {
                int option = Integer.parseInt(s);
                processGamePlayOptions(option);
                break;
            } catch (NumberFormatException e) {
                Utility.clearConsoleScreen();
                System.out.println(Display.errorColour(Constants.INVALID_OPTION_ERROR_MSG));
            } catch (IllegalArgumentException e) {
                Utility.clearConsoleScreen();
                System.out.println(e.getMessage());
            }
        }
    }

    private static void processGamePlayOptions(int optionChosen) {
        if (optionChosen == 1) {
            GameInit.displayGameOptions(); // Start options of game
            return;
        }
        if (optionChosen == 2) {
            OnlineLobbyTemp.displayLobby(); // Start lobby
            return;
        }

        if (optionChosen == 3) {
            checkForPlayerProfiles(); // check player profile to allow chaning profile
            return;
        }
        if (optionChosen == 4) {
            System.out.println(Constants.GOODBYE);
            System.exit(0);
            return;
        }
        throw new IllegalArgumentException(Display.errorColour(" ❌ Please choose a number 1 to 4"));

    }

    /**
     * Checks for existing player profiles in the database and prompts profile
     * creation if none exist.
     */
    private static void checkForPlayerProfiles() {
        Utility.clearConsoleScreen();
        System.out.println(Constants.BANNER);
        System.out.println(Constants.DIVIDER);
        Display.delayPrint("Loading in progress...", 20);

        // DB search what profiles exist
        List<PlayerProfile> players = new PlayerProfileDaoImpl().getAllPlayerProfiles();

        // Show Player Profile Creation if empty
        if (players.isEmpty()) {
            showPlayerProfileCreation(true);
        } else {
            promptForProfileActions(players);
        }
    }

    /**
     * Prompts the user to create a new player profile.
     */
    private static void showPlayerProfileCreation(boolean hasNoProfiles) {
        Utility.clearConsoleScreen();
        System.out.println(Constants.BANNER);
        System.out.println(Constants.DIVIDER);

        // Prompt user to create new user profile
        String message = hasNoProfiles ? "No player profile was detected. " : "Create a new profile?";
        System.out.println(message);
        String name = promptForName();
        System.out.println(Display.colorString("Creating player profile: " + name));

        // Create new value in DB
        PlayerProfileDao ppd = new PlayerProfileDaoImpl();
        if (ppd.createPlayerProfile(name) != null) {
            promptForProfileActions(ppd.getAllPlayerProfiles());
        }
    }

    private static void promptForProfileActions(List<PlayerProfile> playerProfiles) {
        MenuRenderer.displayPlayerProfiles(playerProfiles);
        int numOfProfiles = playerProfiles.size();
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                System.out.print(Display.colorString(" ⚜ Select profile (1 to " + numOfProfiles
                        + ", OR 'c' to create, OR 'd' to delete instead) ➢ "));
                String input = sc.nextLine();
                PlayerProfileOptionInput option = determineUserProfileOptionInput(input);
                processUserProfileOptionInput(playerProfiles, option, input);
                break;
            } catch (NumberFormatException e) {
                System.out.println(Display.errorColour(" ❌ Invalid input"));
            } catch (IllegalArgumentException e) {
                System.out.println(Display.errorColour(e.getMessage()));
            }
        }
    }

    private static void processUserProfileOptionInput(List<PlayerProfile> playerProfiles,
            PlayerProfileOptionInput option, String input) {
        if (option == PlayerProfileOptionInput.CREATE_PROFILE) {
            showPlayerProfileCreation(false);
        }
        if (option == PlayerProfileOptionInput.DELETE_PROFILE) {
            promptForProfileDeletion(playerProfiles);
        } else {
            int profileSelect = Integer.parseInt(input.trim());
            if (profileSelect > playerProfiles.size() || profileSelect <= 0) {
                throw new IllegalArgumentException(" ❌ No such player profile");
            }

            PlayerProfile pp = playerProfiles.get(profileSelect - 1);
            // Set StateManager's current player profile
            StateManager.setPlayerProfile(pp);
            // Show start menu
            StartMenu.showStartMenu();
        }
    }

    private static void promptForProfileDeletion(List<PlayerProfile> players) {
        Scanner sc = new Scanner(System.in);
        System.out.print(Display.errorColour(" ⚜ Select profile to delete ➢ "));
        try {
            int profileToDelete = Integer.parseInt(sc.nextLine());
            if (profileToDelete > players.size() || profileToDelete <= 0) {
                throw new IllegalArgumentException("No such player profile");
            }
            System.out.printf(Display.errorColour("Are you sure you want to delete %s (%d)? ('y'/'N')"),
                    players.get(profileToDelete - 1).getName(), profileToDelete);
            String deletionInput = sc.nextLine().toLowerCase();

            if (deletionInput.equals("y")) {
                new PlayerProfileDaoImpl().deletePlayerProfile(players.get(profileToDelete - 1));
                checkForPlayerProfiles();
            }
        } catch (NumberFormatException e) {
            System.out.println(Display.errorColour(" ❌ Invalid input"));
        } catch (IllegalArgumentException e) {
            System.out.println(Display.errorColour(e.getMessage()));
        }
    }

    private static String promptForName() {
        Scanner sc = new Scanner(System.in);
        String name = "";
        while (true) {
            try {
                System.out.print(Display.colorString(" ⚜ Enter your name: "));
                name = sc.nextLine();
                if (name.isEmpty()) {
                    throw new InputMismatchException(Display.errorColour(" ❌ Please enter an appropriate name."));
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println(e.getMessage());
            }
        }
        return name;
    }

    private static PlayerProfileOptionInput determineUserProfileOptionInput(String input) {
        input = input.trim();
        char operation = input.charAt(0);
        switch (operation) {
            case 'c':
            case 'C':
                return PlayerProfileOptionInput.CREATE_PROFILE;
            case 'd':
            case 'D':
                return PlayerProfileOptionInput.DELETE_PROFILE;
            default:
                return PlayerProfileOptionInput.SELECT_PROFILE;
        }
    }
}
