package com.g2t2.network.networkUtility;

import java.io.IOException;
import java.util.Scanner;

import com.g2t2.game.GameState;
import com.g2t2.game.gameInit.GameInitPrompt;
import com.g2t2.util.Constants;
import com.g2t2.util.Display;
import com.g2t2.util.Utility;

/**
 * This OnlineLobby class represents an online lobby for players to connect and interact before starting a game.
 * It provides methods for users to join or create games, interact in the waiting room, 
 * and allows the GameMaster to manage players (e.g., adding/removing CPUs).
 * 
 * This class is used before the actual gameplay begins and serves as an interface 
 * for multiplayer setup and configuration.
 * 
 * Features:
 * - Displaying waiting room details
 * - Prompting users for commands
 * - GameMaster command control (start game, add/remove CPUs)
 * - Support for dynamic lobby updates via refresh flag
 * 
 * @author Ben
 */

public class OnlineLobby {
    // flag indicating whether the lobby page needs to be refreshed (e.g. a player joined/left)
    private static volatile boolean needToRefreshPage = false; 

    /**
     * Sets the refresh page flag. 
     * If set to true, the page will refresh before user can execute any commands due to lobby updates.
     * 
     * @param setRefreshPage true if the lobby should refresh; false otherwise
     */
    public static void setRefreshPage(boolean setRefreshPage) {
        needToRefreshPage = setRefreshPage;
    }   

    /**
     * Prompts the user to choose between creating a game, joining an existing game,
     * going back, or closing the application.
     *
     * @param username the name of the current user
     * @return a String array representing the user's choice and additional data:
     *         [username, gameId (if joining), command]
     */
    public static String[] joinGamePrompt(String username) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print(Display.colorString(" ➢ Enter your choice: "));

            String[] userChoice = {username, "", ""};
            userChoice[userChoice.length-1] = sc.nextLine();

            if (userChoice[userChoice.length-1].equals(Constants.CREATE_GAME)) {
                return userChoice;

            } else if (userChoice[userChoice.length-1].equals(Constants.JOIN_GAME)) {
                return promptForGameId(username);

            } else if (userChoice[userChoice.length-1].equals(Constants.GO_BACK)) {
                return new String[] {Constants.GO_BACK};

            } else if (userChoice[userChoice.length-1].equals(Constants.CLOSE_APP)) {
                return new String[] {Constants.CLOSE_APP};

            } else {
                System.out.println(Display.errorColour(" ❌ Please input either a valid command"));
                System.out.println(); // reprompt the user
            }
        }
    }

    /**
     * Prompts the user to enter the game ID when they choose to join an existing game.
     *
     * @param username the name of the current user
     * @return a String array containing: [username, entered gameID, JOIN_GAME command]
     */
    private static String[] promptForGameId(String username) {
        Scanner sc = new Scanner(System.in);
        String[] userChoice = {username, "", Constants.JOIN_GAME};

        System.out.println();
        System.out.println(" 📌 To join a game, enter the gameId");
        System.out.print(Display.colorString(" ➢ Enter gameId: "));

        userChoice[1] = sc.nextLine();
        return userChoice;
    }

    /**
     * Displays the current state of the waiting room, including the list of players and available commands. 
     *
     * @param game the current game state
     * @param myPlayerId the playerID of the current user
     */
    public static void printWaitingRoomDetails(GameState game, int myPlayerId) {
        boolean isGameMaster = game.getGameMaster().getPlayerId() == myPlayerId;
        Utility.clearConsoleScreen();
        System.out.println(Constants.ONLINE_WAITING_LOBBY_BANNER);
        printWaitingRoomCommands(isGameMaster, game);
        System.out.println(Constants.DIVIDER);
        System.out.println(" 📌 GAME ID: " + game.getGameId());
        game.printPlayerList();
    }

    /**
     * Displays all available commands in the waiting room.
     * If the user is the gameMaster, additional commands (add/remove CPU, start game) are shown.
     *
     * @param isGameMaster true if the current user is the gameMaster
     * @param game the current game state
     */
    private static void printWaitingRoomCommands(boolean isGameMaster, GameState game) {
        System.out.println(Display.colorString("--- Here are some available commands you can use ---"));
        System.out.println(Constants.LEAVE_GAME_PROMPT);
        if (isGameMaster) { // show available GameMaster Commands
            System.out.println(Display.colorString("--- GameMaster Specific Commands ---"));
            if (game.getNumOfPlayers() < Constants.MAX_PLAYER_NO) {
                System.out.println(Constants.ADD_CPU_PROMPT);
            } 
            if (game.getNumOfCPU() > 0) {
                System.out.println(Constants.REMOVE_CPU_PROMPT);
            } 
            System.out.println(Constants.START_GAME_PROMPT);
        }
    }

    /**
     * Continuously scans for user's commands, such as starting the game, adding/removing CPU players.
     * Reacts to updates if there are any updates to the lobby that is done by other clients
     *
     * @param isGameMaster true if the current user is the gameMaster
     * @param game the current game state
     * @return a String array indicating the selected action and user input data
     * @throws IOException if there is InterruptException caused by threadSleep()
     */
    public static String[] activatingUserControlCommands(boolean isGameMaster, GameState game) throws IOException {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print(Display.colorString(" ➢ Enter command: "));
            String input = sc.nextLine();

            if (needToRefreshPage) { // there is an update to the lobby done by other clients
                needToRefreshPage = false;
                System.out.println("📌 There is an update, refreshing page");
                NetworkUtility.threadSleep(1000);
                return new String[] {Constants.ENTER_KEY};

            // commands available to everyone
            } else if (input.equals(Constants.LEAVE_GAME)) { 
                return new String[] {Constants.LEAVE_GAME};

            } else if (input.equals(Constants.ENTER_KEY)) { // user can manually refresh page
                return new String[] {Constants.ENTER_KEY};

            // commands available to gameMaster only
            } else if (isGameMaster) { 
                if (input.equals(Constants.ADD_CPU)) {
                    if (game.getNumOfPlayers() < Constants.MAX_PLAYER_NO) {
                        String[] CPUInformation = {"", "", Constants.ADD_CPU};
                        String[] userInput = GameInitPrompt.promptForCPUDetails(game.getNumOfPlayers());
                        if (userInput[0].equals(Constants.CANCEL_ACTION)) { // user cancelled action
                            return new String[] {Constants.ENTER_KEY};
                        }
                        System.arraycopy(userInput, 0, CPUInformation, 0, 2);
                        return CPUInformation;
                    }
                } else if (game.getNumOfCPU() > 0 && input.equals(Constants.REMOVE_CPU)) {
                    String[] idsToRemove = GameInitPrompt.promptForCPURemoval(game);
                    if (idsToRemove[0].equals(Constants.CANCEL_ACTION)) {
                        return new String[] {Constants.ENTER_KEY};
                    }
                    return idsToRemove;

                } else if (input.equals(Constants.START_GAME)) {
                    if (game.getNumOfPlayers() < Constants.MIN_PLAYER_NO) {
                        System.out.println(Display.errorColour(" ❌ Insufficient Number of Players"));
                        continue;
                    } else  {
                        return new String[] {Constants.START_GAME};
                    }
                }
            }
            System.out.println(Constants.INVALID_COMMAND);
        }
    }

    /**
     * Prints the joining game lobby header
     */
    public static void joiningGameLobbyHeader() {
        Utility.clearConsoleScreen();
        System.out.println(Constants.ONLINE_LOBBY_BANNER);
        System.out.println(Constants.DIVIDER);
        System.out.println(Constants.CLOSE_APP_PROMPT);
        System.out.println(Constants.GO_BACK_PROMPT);
        System.out.println(Constants.CREATE_GAME_PROMPT);
        System.out.println(Constants.JOIN_GAME_PROMPT);
    }
}
