package com.g2t2.network.networkUtility;

import java.net.ContentHandler;
import java.util.Scanner;

import com.g2t2.game.GameState;
import com.g2t2.game.gameInit.GameInitPrompt;
import com.g2t2.util.Constants;
import com.g2t2.util.Display;
import com.g2t2.util.Utility;

/**
 * Represents an online lobby for players to connect and interact before starting a game.
 *
 * This class provides methods to display the lobby and update player information.
 * It serves as the entry point for players in an online multiplayer environment.
 * 
 * @author Ben
 */

public class OnlineLobby {
    private static volatile boolean needToRefreshPage = false; 

    public static void setRefreshPage(boolean setRefreshPage) {
        needToRefreshPage = setRefreshPage;
    }   

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
                // TODO (leave server and go to main menu)

            } else if (userChoice[userChoice.length-1].equals(Constants.CLOSE_APP)) {
                return new String[] {Constants.CLOSE_APP};

            } else {
                System.out.println(Display.errorColour(" ❌ Please input either a valid command"));
                System.out.println(); // reprompt the user
            }
        }
    }

    private static String[] promptForGameId(String username) {
        Scanner sc = new Scanner(System.in);
        String[] userChoice = {username, "", Constants.JOIN_GAME};

        System.out.println();
        System.out.println(" 📌 To join a game, enter the gameId");
        System.out.print(Display.colorString(" ➢ Enter gameId: "));

        userChoice[1] = sc.nextLine();
        return userChoice;
    }

    public static void printWaitingRoomDetails(GameState game, int myPlayerId) {
        boolean isGameMaster = game.getGameMaster().getPlayerId() == myPlayerId;
        Utility.clearConsoleScreen();
        System.out.println(Constants.ONLINE_WAITING_LOBBY_BANNER);
        printWaitingRoomCommands(isGameMaster, game);
        System.out.println(Constants.DIVIDER);
        System.out.println(" 📌 GAME ID: " + game.getGameId());
        game.printPlayerList();
    }

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

    public static String[] activatingUserControlCommands(boolean isGameMaster, GameState game) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print(Display.colorString(" ➢ Enter command: "));
            String input = sc.nextLine();

            if (needToRefreshPage) { // someone joins
                needToRefreshPage = false;
                System.out.println("📌 There is an update, refreshing page");
                NetworkUtility.threadSleep(1000);
                return new String[] {Constants.ENTER_KEY};

                // commands available to everyone
            } else if (input.equals(Constants.LEAVE_GAME)) { 
                return new String[] {Constants.LEAVE_GAME};

            } else if (input.equals(Constants.ENTER_KEY)) { // refresh page
                return new String[] {Constants.ENTER_KEY};

            } else if (isGameMaster) { // commands available to gameMaster
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

    
}
