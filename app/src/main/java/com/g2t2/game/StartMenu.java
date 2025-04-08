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
import com.g2t2.game.gameutility.GameRenderer;
import com.g2t2.network.client.Client;
import com.g2t2.types.Card;
import com.g2t2.types.PlayerProfile;
import com.g2t2.util.Constants;
import com.g2t2.util.Display;
import com.g2t2.util.Utility;

/**
 * 
 * The StartMenu class is responsible for displaying and managing the initial user interface 
 * of the game application. It acts as the main entry point for players, allowing them to 
 * navigate between various gameplay and profile management options.
 * 
 * This class handles input validation, profile loading from the database, and the appropriate 
 * redirection based on the selected option. It ensures that a valid player profile is set 
 * before allowing gameplay to begin.
 * 
 * This class is the first to be invoked when the application starts, and it guides 
 * the user through the necessary setup before entering gameplay.
 * 
 * @author Oliver
 */
public class StartMenu {
    private static final int portNumber = 1234; // ensure that this portNumber matches the one of the server
    private static final String serverAddress = "localhost"; // to execute on different devices, change to the IP address of the server
    private static final String WELCOMEMESSAGE_STRING = " ⚜ Welcome to G2T2 Parade! Please select an option.";
    private static final String GAMEPLAY_OPTIONS = " 📌 Play local (1)\n" 
                                                 + " 📌 Play online (2)\n"
                                                 + " 📌 Change profile (3)\n"
                                                 + " 📌 Learn how to play (4) \n"
                                                 + " 📌 Exit application (5)\n"
                                                 + Display.colorString(" ⚜ Enter option ➢ ");

    /**
     * Displays the start menu of the application based on the current mode (GUI or console).
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

    /**
     * Displays the main game menu and prompts the user to select a gameplay option.
     */
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

    /**
     * Processes the user's selection from the main game menu.
     * 
     * This method handles the following options:
     * 1. Play local game - Initializes a local game with configuration options
     * 2. Play online game - Attempts to connect to the server and start an online game session
     * 3. Change profile - Displays profile management options
     * 4. Learn how to play - Displays the game manual
     * 5. Exit application - Terminates the application
     * 
     * @param optionChosen the numeric option selected by the user
     * @throws IllegalArgumentException if the selected option is not within the valid range (1-5)
     */
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
                GameRenderer.temporaryPauseScreen(" ⚜ Enter any key to continue ➢ ");
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

    /**
     * Displays the game manual, providing detailed instructions on how to play the game.
     */
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

        GameRenderer.temporaryPauseScreen(" ⚜ Enter any key to go back to the Menu ➢ ");
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
     * Displays the profile creation interface and handles the creation of new player profiles.
     * 
     * The user can:
     * - Enter a name for the new profile
     * - Cancel the operation by entering the cancel action command
     * - Confirm or reject the profile creation
     * 
     * If profile creation is successful, the method updates the player profile list
     * and returns to the profile actions menu.
     * 
     * @param hasNoProfiles  boolean indicating whether this is the first profile {@code true} 
     *                       or an additional profile {@code false}
     * @param playerProfiles list of existing player profiles to return to if operation is cancelled
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

    /**
     * Displays available player profiles and prompts the user to select a profile 
     * or perform profile management actions.
     * 
     * This method presents a list of existing player profiles and offers the following options:
     * - Select a profile (by entering its number)
     * - Create a new profile (by entering 'c')
     * - Delete an existing profile (by entering 'd')
     * - Exit the application (by entering the exit command)
     * 
     * When a profile is successfully selected, it is set as the current player 
     * profile in the StateManager.
     * 
     * @param playerProfiles the list of available player profiles to display and manage
     */
    private static void promptForProfileActions(List<PlayerProfile> playerProfiles) {
        MenuRenderer.displayPlayerProfiles(playerProfiles);
        int numOfProfiles = playerProfiles.size();
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                System.out.println(Constants.CLOSE_APP_PROMPT);
                System.out.print(Display.colorString(" ⚜ Enter profile (1 to " + numOfProfiles
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

    /**
     * Processes the user's profile management action based on their selection.
     * 
     * This method handles the following profile management options:
     * - Creating a new profile when the CREATE_PROFILE option is selected
     * - Deleting an existing profile when the DELETE_PROFILE option is selected
     * - Selecting and setting a profile as the current active profile
     * 
     * When a profile is selected, it is set as the current profile in the StateManager
     * and the start menu is displayed.
     * 
     * @param playerProfiles the list of available player profiles
     * @param option         the profile action option
     * @param input          the original user input string
     */
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

    /**
     * Displays a prompt for selecting and deleting a player profile.
     * 
     * This method allows users to:
     * - Select a profile for deletion by entering its number
     * - Cancel the deletion operation by entering the cancel action command
     * - Confirm or reject the deletion with a secondary confirmation prompt
     * 
     * @param playerProfiles the list of available player profiles
     */
    private static void promptForProfileDeletion(List<PlayerProfile> playerProfiles) {
        Scanner sc = new Scanner(System.in);
        System.out.println();
        System.out.println(Constants.PROMPT_CANCEL_ACTION);
        while(true) {
            System.out.print(Display.errorColour(" ⚜ Enter profile to delete (" + 1 + "-" + playerProfiles.size() + ") ➢ "));
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
    }

    /**
     * Determines the profile management action based on the user's input.
     * 
     * This method analyzes the the user's input to identify which profile 
     * action they intend to take:
     * - 'c': Create a new profile
     * - 'd': Delete an existing profile
     * - Any other input: Select a profile (assumed to be a numeric profile index)
     * 
     * @param input the user's input string
     * @return a PlayerProfileOptionInput enum value representing the determined action
     */
    private static PlayerProfileOptionInput determineUserProfileOptionInput(String input) {
        input = input.trim().toLowerCase();
        char operation = input.charAt(0);
        switch (operation) {
            case 'c':
                return PlayerProfileOptionInput.CREATE_PROFILE;
            case 'd':
                return PlayerProfileOptionInput.DELETE_PROFILE;
            default:
                return PlayerProfileOptionInput.SELECT_PROFILE;
        }
    }
}
