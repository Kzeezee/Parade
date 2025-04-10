package com.g2t2.game.gameinit;

import java.util.Scanner;

import com.g2t2.util.Constants;
import com.g2t2.util.Display;
import com.g2t2.util.Utility;
import com.g2t2.game.LocalGamePlay;
import com.g2t2.game.GameState;
import com.g2t2.game.StartMenu;
import com.g2t2.game.StateManager;
import com.g2t2.types.Player;

/**
 * Class responsible for initializing the game settings, players, and parade
 * board.
 * 
 * This class handles all pre-game setup, including configuring the number of
 * players,
 * assigning CPU players, setting difficulty levels, and distributing cards to
 * players.
 * It ensures that the game is properly initialized before gameplay begins.
 * 
 * @author Oliver
 * @author Ben
 */
public class GameInit {

    /**
     * Displays the game options in console, while requesting
     * the user to configure the game via console prompts.
     */
    public static void displayGameOptions() {
        GameState game = new GameState();
        // add host first
        game.addPlayer(new Player(StateManager.getPlayerProfile()));

        displayWaitingRoomOptions(game);
    }

    /**
     * Displays configurable game options in the console.
     * 
     * This method allows users to specify the total number of players,
     * assign CPU players, and set the difficulty level for CPU-controlled players.
     * It validates user input to ensure proper configuration.
     * 
     * @param game the current game state
     */
    public static void displayWaitingRoomOptions(GameState game) {
        boolean startGame = false;
        // keep returning to lobby until game starts
        while (!startGame) {
            printWaitingRoomDetails(game);
            startGame = activatingUserControlCommands(game);
        }
        LocalGamePlay.startGame(game);
    }

    /**
     * Displays available commands that the user can enter in the waiting room.
     * 
     * This method shows different commands based on the current game state:
     * - ADD_PLAYER and ADD_CPU options are shown only if the maximum player limit hasn't been reached
     * - REMOVE_CPU option is shown only if there are CPU players in the game
     * - START_GAME and LEAVE_GAME options are always available
     * 
     * @param game the current game state
     */
    private static void printWaitingRoomCommands(GameState game) {
        System.out.println(Display.colorString("--- Here are some available commands you can use ---"));
        if (game.getNumOfPlayers() < Constants.MAX_PLAYER_NO) {
            System.out.println(" 📌 To add local player(s), enter '" + Display.colorString(Constants.ADD_PLAYER) + "'");
            System.out.println(" 📌 To add CPU(s), enter '" + Display.colorString(Constants.ADD_CPU) + "'");
        }
        if (game.getNumOfPlayers() > 1) {
            System.out.println(" 📌 To remove local player/CPU(s), enter '" + Display.colorString(Constants.REMOVE_PLAYER) + "'");
        }
        System.out.println(" 📌 To start the game, enter '" + Display.colorString(Constants.START_GAME) + "'");
        System.out.println(" 📌 To exit the lobby, enter '" + Display.colorString(Constants.LEAVE_GAME) + "'");

    }

    /**
     * Processes user commands in the waiting room and performs the corresponding actions.
     * 
     * This method handles various user commands including:
     * - LEAVE_GAME: Resets player counters and returns to the start menu
     * - ADD_PLAYER: Prompts for a player name and adds the player to the game
     * - ADD_CPU: Prompts for CPU details and adds CPU players to the game
     * - REMOVE_CPU: Prompts for CPU IDs to remove and removes them from the game
     * - START_GAME: Validates player count and starts the game if sufficient
     * 
     * Each command performs its action and returns a boolean indicating whether
     * the game should start ({@code true}) or return to the waiting room ({@code false}).
     * 
     * @param game the current game state
     * @return boolean value indicating whether to start the game (true) or return to waiting room (false)
     */
    private static boolean activatingUserControlCommands(GameState game) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print(Display.colorString(" ➢ Enter command: "));
            String input = sc.nextLine();
            boolean hasLowerCases = input.matches(".*[a-z].*");
            if (hasLowerCases) {
                System.out.println(Display.errorColour(" ❌ Enter command in all CAPITAL letters\n"));
                continue;
            } else if (input.equals(Constants.LEAVE_GAME)) {
                Player.resetCounter();
                StartMenu.showStartMenu();
            } else if (input.equals(Constants.ADD_PLAYER) && game.getNumOfPlayers() < Constants.MAX_PLAYER_NO) {
                String name = GameInitPrompt.promptForPlayerName();
                if (name.equals(Constants.CANCEL_ACTION)) {
                    return false;
                }
                createPlayer(name, game);
                return false;
            } else if (input.equals(Constants.ADD_CPU) && game.getNumOfPlayers() < Constants.MAX_PLAYER_NO) {
                String[] userInput = GameInitPrompt.promptForCPUDetails(game.getNumOfPlayers());
                if (userInput[0].equals(Constants.CANCEL_ACTION)) {
                    return false;
                }
                createCPU(Integer.parseInt(userInput[0]), Integer.parseInt(userInput[1]), game);
                return false;
            } else if (game.getNumOfCPU() > 0 && input.equals(Constants.REMOVE_PLAYER)) {
                String[] idsToRemove = GameInitPrompt.promptForPlayerRemoval(game);
                if (idsToRemove[0].equals(Constants.CANCEL_ACTION)) {
                    return false;
                }
                removePlayers(idsToRemove, game);
                return false;
            } else if (input.equals(Constants.START_GAME)) {
                if (game.getNumOfPlayers() < Constants.MIN_PLAYER_NO) {
                    System.out.println(Display.errorColour(" ❌ Insufficient Number of Players"));
                    System.out.println();
                    continue;
                } else {
                    return true;
                }
            } 

            System.out.println(Display.errorColour(" ❌ Invalid Command"));
            System.out.println();
            continue;
        }
    }

    /**
     * Displays configurable game options in the console.
     * 
     * This method allows users to specify the total number of players,
     * assign CPU players, and set the difficulty level for CPU-controlled players.
     * It validates user input to ensure proper configuration.
     * 
     * @param game the current game state
     */
    public static void printWaitingRoomDetails(GameState game) {
        Utility.clearConsoleScreen();
        System.out.println(Constants.LOCAL_WAITING_LOBBY_BANNER);
        printWaitingRoomCommands(game);
        System.out.println(Constants.DIVIDER);
        game.printPlayerList();
    }

    /**
     * Creates a new player with the given name and adds them to the game.
     * 
     * This method initializes a new Player object with the specified name,
     * sets the player's profile to the current player profile from StateManager,
     * and adds the player to the current game state.
     * 
     * @param name the name of the player
     * @param game the current game state
     * @return the newly created Player object
     */
    public static Player createPlayer(String name, GameState game) {
        Player player = new Player(name);
        player.setPlayerProfile(StateManager.getPlayerProfile());
        game.addPlayer(player);
        return player;
    }

    /**
     * Creates the specified number of CPU players with the given difficulty level.
     * 
     * This method creates multiple CPU players and adds them to the game. Each CPU
     * is initialized with the specified difficulty level and automatically assigned
     * a unique name based on the CPU counter.
     * 
     * @param numOfCPU the number of CPU players to create
     * @param difficulty the difficulty level for the CPU players (1=easy, 2=medium, 3=hard)
     * @param game the current game state
     */
    public static void createCPU(int numOfCPU, int difficulty, GameState game) {
        for (int i = 0; i < numOfCPU; i++) {
            Player cpu = new Player(true, difficulty);
            game.addPlayer(cpu);
        }
    }

    /**
     * Removes multiple players from the game based on their player IDs.
     * 
     * This method processes an array of player IDs and removes each corresponding player
     * from the game state.
     * 
     * @param ids an array of player ID strings
     * @param game the current game state
     */
    public static void removePlayers(String[] ids, GameState game) {
        for (int i = 0; i < ids.length - 1; i++) {
            game.removePlayer(game.findMyPlayer(Integer.parseInt(ids[i])));
        }
    }


}
