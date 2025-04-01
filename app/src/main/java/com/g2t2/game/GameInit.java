package com.g2t2.game;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

import com.g2t2.util.Constants;
import com.g2t2.util.Display;
import com.g2t2.util.Utility;

import com.g2t2.types.ParadeBoard;
import com.g2t2.types.Player;
import com.g2t2.types.PlayerProfile;
import com.g2t2.game.gameutility.Prompt;
import com.g2t2.types.Card;

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
    private static int numOfPlayers; // The total number of players in the game (including human and CPU players).
    private static int numOfCPU; // The number of CPU-controlled players in the game.

    /**
     * The difficulty level for CPU players:
     * 1 - Easy
     * 2 - Medium
     * 3 - Hard
     */


    /**
     * Displays the game options either in console
     * 
     * It will configure the game via console prompts.
     * 
     */
    public static void displayGameOptions() {
        // Console configuration
        GameState game = new GameState();
        displayConsoleGameOptions(game);
        initialiseGame(game);
        GamePlay.startGame(game);
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
    private static void displayConsoleGameOptions(GameState game) {
        numOfPlayers = 0;
        numOfCPU = 0;
        Utility.clearConsoleScreen();
        System.out.println(Constants.BANNER);
        System.out.println(Constants.DIVIDER);
        numOfPlayers = GameInitPrompt.promptForNumberofPlayers();
        numOfCPU = GameInitPrompt.promptForCPUPlayerNumber(numOfPlayers);
    }

    /**
     * Initializes the players and parade board for the game.
     * 
     * 
     *
     * @param game the current game state
     */
    private static void initialiseGame(GameState game) {
        Utility.clearConsoleScreen();
        System.out.println(Constants.BANNER);

        // Get first player name from player profile
        PlayerProfile pp = StateManager.getPlayerProfile();
        Player player = new Player(pp.getName());
        player.setPlayerProfile(pp);
        game.addPlayer(player); // User's player

        // Handle rest of the local players name input
        for (int i = 2; i <= numOfPlayers - numOfCPU; i++) {
            GameInitPrompt.promptForPlayerName(game, i);
        }

        // choosing difficulty level and adding the cpu players
        for (int i = 0; i < numOfCPU; i++) {
            GameInitPrompt.promptForCPUDifficulty(game);
        }

        game.printPlayerList();
        game.giveOutInitialCards();
    }

   
 
}
