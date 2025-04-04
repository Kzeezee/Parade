package com.g2t2.game.gameInit;

import com.g2t2.util.Constants;
import com.g2t2.util.Display;
import com.g2t2.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import com.g2t2.game.GamePlay;
import com.g2t2.game.GameState;
import com.g2t2.game.StartMenu;
import com.g2t2.game.StateManager;
import com.g2t2.network.networkUtility.NetworkUtility;
import com.g2t2.types.Player;
import com.g2t2.types.PlayerProfile;

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
     * Displays the game options either in console
     * 
     * It will configure the game via console prompts.
     * 
     */
    public static void displayGameOptions() {
        GameState game = new GameState();
        // add host first
        PlayerProfile pp = StateManager.getPlayerProfile();
        Player player = new Player(pp.getName());
        player.setPlayerProfile(pp);
        game.addPlayer(player);

        displayWaitingRoomOptions(game);
    }

    /**
     * Displays configurable game options in the console.
     * 
     * This method allows users to specify the total number of players,
     * assign CPU players, and set the difficulty level for CPU-controlled players.
     * It validates user input to ensure proper configuration.
     * 
     * @author Oliver
     */
    private static void displayWaitingRoomOptions(GameState game) {
        boolean startGame = false;
        // keep returning to lobby until game starts
        while (!startGame){
            printWaitingRoomDetails(game);
            startGame = activatingUserControlCommands(game);
        }
        GamePlay.startGame(game);
    }

    
    public static void printWaitingRoomDetails(GameState game) {
        Utility.clearConsoleScreen();
        System.out.println(Constants.WAITING_LOBBY_BANNER);
        printWaitingRoomCommands(game);
        System.out.println(Constants.DIVIDER);
        game.printPlayerList();
    }
    
    private static void printWaitingRoomCommands(GameState game) {
        System.out.println(Display.colorString("--- Here are some available commands you can use ---"));
        if (game.getNumOfPlayers() < Constants.MAX_PLAYER_NO) {
            System.out.println(" 📌 To add player(s), enter '" + Display.colorString(Constants.ADD_PLAYER) + "'");
            System.out.println(" 📌 To add CPU(s), enter '" + Display.colorString(Constants.ADD_CPU) + "'");
        } 
        if (game.getNumOfCPU() > 0) {
            System.out.println(" 📌 To remove CPU(s), enter '" + Display.colorString(Constants.REMOVE_CPU) + "'");
        } 
        System.out.println(" 📌 To start the game, enter '" + Display.colorString(Constants.START_GAME) + "'");
        System.out.println(" 📌 To exit the lobby, enter '" + Display.colorString(Constants.LEAVE_GAME) + "'");
     
    }

    public static boolean activatingUserControlCommands(GameState game) /*throws InterruptedException*/ {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print(Display.colorString(" ➢ Enter command: "));
            String input = sc.nextLine();
            if (input.equals(Constants.LEAVE_GAME)){
                StartMenu.showStartMenu();
            } else if (input.equals(Constants.ADD_PLAYER) && game.getNumOfPlayers() < Constants.MAX_PLAYER_NO){
                    createPlayer(GameInitPrompt.promptForPlayerName(), game);
                    return false;
            } else if (input.equals(Constants.ADD_CPU) && game.getNumOfPlayers() < Constants.MAX_PLAYER_NO) {
                    String[] userInput = GameInitPrompt.promptForCPUDetails(game.getNumOfPlayers());
                    createCPU(Integer.parseInt(userInput[0]), Integer.parseInt(userInput[1]), game);
                    return false;
            } else if (game.getNumOfCPU() > 0 && input.equals(Constants.REMOVE_CPU)) {
                    String[] idsToRemove = GameInitPrompt.promptForCPURemoval(game);
                    removePlayers(idsToRemove, game);
                    return false;
            } else if (input.equals(Constants.START_GAME)) {
                    if (game.getNumOfPlayers() < Constants.MIN_PLAYER_NO) {
                        System.out.println(Display.errorColour(" ❌ Insufficient Number of Players"));
                        continue;
                    } else  {
                        return true;
                    }
            }
            
            System.out.println(Display.errorColour(" ❌ Invalid Command"));
            continue;
        }
    }
   

    public static Player createPlayer(String name, GameState game) {
        Player player = new Player(name);
        player.setPlayerProfile(StateManager.getPlayerProfile());
        game.addPlayer(player);
         return player;
    }

    public static void createCPU(int numOfCPU, int difficulty, GameState game) {
        for (int i = 0; i < numOfCPU; i++ ) {
        Player cpu = new Player(true, difficulty);
        game.addPlayer(cpu);
        }
    }

    public static void removePlayers(String[] ids, GameState game) {
        for (int i = 0; i < ids.length - 1; i++) {
            game.removePlayer(game.findMyPlayer(Integer.parseInt(ids[i])));
        }
    }
}
