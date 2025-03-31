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
        if (StateManager.getPlayerProfile() == null) {
            checkForPlayerProfiles();
        } else {
            consoleStartMenu();
        }
    }

    /**
     * Allows the player to select a profile to play on, enabling progress tracking.
     * 
     * @param players List of available player profiles.
     */
    private static void selectPlayerProfile(List<PlayerProfile> players) {
        MenuRenderer.displayPlayerProfiles(players);
        System.out.println(" 📌 Select your Parade profile (" + 1 + "-" + players.size() + ")");

        Scanner sc = new Scanner(System.in);
        boolean validOptionSelected = false;
        while (!validOptionSelected) {
            try {
                System.out.print(Display.colorString(" ⚜ Select profile ('c' to create, 'd' to delete instead) ➢ "));
                System.out.print(Constants.ANSI_RESET);

                String input = sc.nextLine();
                PlayerProfileOptionInput option = determineUserProfileOptionInput(input);
                if (option == PlayerProfileOptionInput.CREATE_PROFILE) {
                    validOptionSelected = true;
                    showPlayerProfileCreation(false);
                }
                if (option == PlayerProfileOptionInput.DELETE_PROFILE) {
                    System.out.print(Display.errorColour(" ⚜ Select profile to delete ➢ "));
                    try {
                        int profileToDelete = Integer.parseInt(sc.nextLine());
                        if (profileToDelete > players.size() || profileToDelete <= 0) {
                            throw new IllegalArgumentException("No such player profile");
                        }
                        System.out.printf(Display.errorColour("Are you sure you want to delete %s (%d)? ('y'/'N')"), players.get(profileToDelete - 1).getName(), profileToDelete);
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
                } else {
                    int profileSelect = Integer.parseInt(input.trim());
                    if (profileSelect > players.size() || profileSelect <= 0) {
                        throw new IllegalArgumentException(" ❌ No such player profile");
                    }

                    validOptionSelected = true;
                    PlayerProfile pp = players.get(profileSelect - 1);
                    // Set StateManager's current player profile
                    StateManager.setPlayerProfile(pp);
                    // Show start menu
                    StartMenu.showStartMenu();
                }
            } catch (NumberFormatException e) {
                System.out.println(Display.errorColour(" ❌ Invalid input"));
            } catch (IllegalArgumentException e) {
                System.out.println(Display.errorColour(e.getMessage()));
            }
        }
    }

    /**
     * Displays the console-based start menu and handles user input.
     */
    private static void consoleStartMenu() {
        // Utility.clearConsoleScreen();
        Scanner sc = new Scanner(System.in);
        int optionChosen = 0;

        boolean validOptionSelected = false;
        while (!validOptionSelected) {
            Utility.clearConsoleScreen();
            System.out.println(Constants.ANSI_RESET);
            System.out.println(Constants.BANNER);
            System.out.println(Constants.DIVIDER);
            System.out.println(WELCOMEMESSAGE_STRING);
            System.out.print(GAMEPLAY_OPTIONS);
            String s = sc.nextLine();
            System.out.print(Constants.ANSI_RESET);
            System.out.println("Triggered here");
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
                        // Change player profile
                        checkForPlayerProfiles();
                        break;
                    case 4:
                        System.out.println(Constants.GOODBYE);
                        System.exit(0);
                    default:
                        System.out.println("Entered : " + s);
                        throw new IllegalArgumentException(Display.errorColour(" ❌ Invalid input"));
                }
            } catch (NumberFormatException e) {
                Utility.clearConsoleScreen();
                System.out.println("Number format here");
                System.out.println(Display.errorColour(Constants.INVALID_OPTION_ERROR_MSG));
            } catch (IllegalArgumentException e) {
                Utility.clearConsoleScreen();
                System.out.println("Illegal argument here");
                System.out.println(Display.errorColour(Constants.INVALID_OPTION_ERROR_MSG));
            }
        }
    }

    /**
     * Checks for existing player profiles in the database and prompts profile
     * creation if none exist.
     */
    private static void checkForPlayerProfiles() {
        Utility.clearConsoleScreen();
        System.out.println(Constants.ANSI_RESET);
        System.out.println(Constants.BANNER);
        System.out.println(Constants.DIVIDER);
        Display.delayPrint("Loading in progress...", 30);
        System.out.println(Constants.ANSI_RESET);

        // DB search what profiles exist
        List<PlayerProfile> players = new PlayerProfileDaoImpl().getAllPlayerProfiles();

        // Show Player Profile Creation if empty
        if (players.isEmpty()) {
            showPlayerProfileCreation(true);
        } else {
            selectPlayerProfile(players);
        }
    }

    /**
     * Prompts the user to create a new player profile.
     */
    private static void showPlayerProfileCreation(boolean noProfiles) {
        Utility.clearConsoleScreen();
        System.out.println(Constants.ANSI_RESET);
        System.out.println(Constants.BANNER);
        System.out.println(Constants.DIVIDER);
        Scanner sc = new Scanner(System.in);

        Utility.clearConsoleScreen();
        // Prompt user to create new user profile
        String message = "";
        if (noProfiles)
            message += "No player profile was detected. ";
        message += "Create a new profile?";
        System.out.println(Display.colorString(message));
        String name = "";
        while (true) {
            try {
                System.out.print(Display.colorString(" ⚜ Enter your name: "));
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
        PlayerProfileDao ppd = new PlayerProfileDaoImpl();
        if (ppd.createPlayerProfile(name) != null) {
            selectPlayerProfile(ppd.getAllPlayerProfiles());
        }
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
