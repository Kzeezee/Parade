package com.g2t2.game;

import java.util.Scanner;
import java.util.List;
import java.net.Socket;
import java.io.IOException;

import com.g2t2.db.dao.PlayerProfileDao;
import com.g2t2.db.dao.impl.PlayerProfileDaoImpl;
import com.g2t2.enums.CardColour;
import com.g2t2.enums.PlayerProfileOptionInput;
import com.g2t2.game.gameInit.GameInit;
import com.g2t2.game.gameInit.GameInitPrompt;
import com.g2t2.game.gameutility.GamePlayPrompt;
import com.g2t2.network.client.Client;
import com.g2t2.network.networkUtility.NetworkUtility;
import com.g2t2.types.Card;
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
    private static final int portNumber = 1234;
    private static final String serverAddress = "localhost";
    private static final String WELCOMEMESSAGE_STRING = " ⚜ Welcome to G2T2 Parade! Please select an option.";
    private static final String GAMEPLAY_OPTIONS = " 📌 Play local (1)\n" 
                                                 + " 📌 Play online (2)\n"
                                                 + " 📌 Change profile (3)\n"
                                                 + " 📌 Learn how to play (4) \n"
                                                 + " 📌 Exit application (5)\n"
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
            System.out.println(WELCOMEMESSAGE_STRING);
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
            try {
                Socket socket = new Socket(serverAddress, portNumber);
                Client client = new Client(socket, StateManager.getPlayerProfile().getName());
                client.start();
            } catch (IOException e) {
                System.out.println(Display.errorColour(" ❌ Server is closed"));
                GamePlay.temporaryPauseScreen(" ⚜ Enter any key to continue ➢ ");
            }
            consoleStartMenu();
            return;
        }

        if (optionChosen == 3) {
            checkForPlayerProfiles(); // check player profile to allow changing of profile
            return;
        }
        if (optionChosen == 4) {
            displayGameManual();
            promptForGamePlayOptions();
            return;
        }
        if (optionChosen == 5) {
            Utility.exitGame();
            return;
        }
        throw new IllegalArgumentException(Display.errorColour(" ❌ Please choose a number 1 to 5"));

    }

    private static void displayGameManual() {
        Utility.clearConsoleScreen();
        System.out.println(Constants.DIVIDER);
        System.out.println(Constants.GAME_INSTRUCTION_OBJ);

        System.out.println(Constants.GAME_INSTRUCTION_COMPONENT);

        System.out.println(Display.colorString("⚡︎ EXAMPLES ⚡︎"));
        Display.displayCardsSideBySide(List.of(new Card(1, CardColour.BLACK), new Card(2, CardColour.BLUE),
                new Card(3, CardColour.GREEN), new Card(4, CardColour.PURPLE),
                new Card(1, CardColour.RED), new Card(5, CardColour.YELLOW)), true);

        System.out.println(Constants.DIVIDER);

        System.out.println(Constants.GAME_INSTRUCTION_SETUP);


        System.out.println(Constants.GAME_INSTRUCTION_PLAY);

        System.out.println(Constants.GAME_INSTRUCTION_SPECIAL_RULES);

        System.out.println(Constants.GAME_INSTRUCTION_ENDGAME);

        System.out.println(Constants.GAME_INSTRUCTION_SCORING);

        GamePlay.temporaryPauseScreen(" ⚜ Enter any key to go back to the Menu ➢ ");
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
            showPlayerProfileCreation(true, players);
        } else {
            promptForProfileActions(players);
        }
    }

    /**
     * Prompts the user to create a new player profile.
     */
    private static void showPlayerProfileCreation(boolean hasNoProfiles, List<PlayerProfile> playerProfiles) {
        Utility.clearConsoleScreen();
        System.out.println(Constants.BANNER);
        System.out.println(Constants.DIVIDER);

        // Prompt user to create new user profile
        String message = hasNoProfiles ? "No player profile was detected. " : "Create a new profile?";
        System.out.println(message);
        String name = GameInitPrompt.promptForPlayerName();
        if (name.equals(Constants.CANCEL_ACTION)) {
            promptForProfileActions(playerProfiles);
        }

        if (GamePlayPrompt.confirmsChoice()) {
            System.out.println(Display.colorString("Creating player profile: " + name));

            // Create new value in DB
            PlayerProfileDao ppd = new PlayerProfileDaoImpl();
            if (ppd.createPlayerProfile(name) != null) {
                promptForProfileActions(ppd.getAllPlayerProfiles());
            }
        }
        promptForProfileActions(playerProfiles);
    }

    private static void promptForProfileActions(List<PlayerProfile> playerProfiles) {
        MenuRenderer.displayPlayerProfiles(playerProfiles);
        int numOfProfiles = playerProfiles.size();
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                System.out.println(Constants.CLOSE_APP_PROMPT);
                System.out.print(Display.colorString(" ⚜ Select profile (1 to " + numOfProfiles
                        + ", OR 'c' to create, OR 'd' to delete instead) ➢ "));
                String input = sc.nextLine();

                // handling input errors
                if (input.equals(Constants.CLOSE_APP)) {
                    Utility.exitGame();
                } else if (input.length() < 1) {
                    System.out.println(Display.errorColour(" ❌ Please enter something"));
                    System.out.println();
                    continue;
                }
    
                PlayerProfileOptionInput option = determineUserProfileOptionInput(input);

                // handling wrong profile number
                if (option.equals(PlayerProfileOptionInput.SELECT_PROFILE)) {
                    int intInput = Integer.parseInt(input);
                    if (intInput < 1 || intInput > numOfProfiles) {
                        System.out.println(Display.errorColour(" ❌ No such player profile. Please enter a number between 1 to " + numOfProfiles));
                        System.out.println();
                        continue;
                    }
                }   
                processUserProfileOptionInput(playerProfiles, option, input);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(Display.errorColour(" ❌ Please enter a valid command ('c' / 'd')"));
                System.out.println();
            }
        }
    }

    private static void processUserProfileOptionInput(List<PlayerProfile> playerProfiles,
            PlayerProfileOptionInput option, String input) {
        if (option == PlayerProfileOptionInput.CREATE_PROFILE) {
            showPlayerProfileCreation(false, playerProfiles);
        }
        if (option == PlayerProfileOptionInput.DELETE_PROFILE) {
            promptForProfileDeletion(playerProfiles);
        } else {
            int profileSelect = Integer.parseInt(input.trim());
            PlayerProfile pp = playerProfiles.get(profileSelect - 1);

            // Set StateManager's current player profile
            StateManager.setPlayerProfile(pp);
            // Show start menu
            StartMenu.showStartMenu();
        }
    }

    private static void promptForProfileDeletion(List<PlayerProfile> playerProfiles) {
        Scanner sc = new Scanner(System.in);
        System.out.println();
        System.out.println(Constants.PROMPT_CANCEL_ACTION);
        System.out.print(Display.errorColour(" ⚜ Select profile to delete ➢ "));
        try {
            String input = sc.nextLine();
            if (input.equals(Constants.CANCEL_ACTION)) {
                promptForProfileActions(playerProfiles);
            }
            int profileToDelete = Integer.parseInt(input);
            if (profileToDelete > playerProfiles.size() || profileToDelete <= 0) {
                throw new IllegalArgumentException(" ❌ No such player profile");
            }
            System.out.printf(Display.errorColour(" ⚜ Are you sure you want to delete %s (%d)? ('y'/'n') ➢ "),
                                                playerProfiles.get(profileToDelete - 1).getName(), profileToDelete);
            String deletionInput = sc.nextLine().toLowerCase();

            if (deletionInput.equals("y")) {
                new PlayerProfileDaoImpl().deletePlayerProfile(playerProfiles.get(profileToDelete - 1));
                checkForPlayerProfiles();
            }
        } catch (NumberFormatException e) {
            System.out.println(Display.errorColour(" ❌ Invalid input"));
        } catch (IllegalArgumentException e) {
            System.out.println(Display.errorColour(e.getMessage()));
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
