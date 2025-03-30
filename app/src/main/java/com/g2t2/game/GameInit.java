package com.g2t2.game;

import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

import com.g2t2.util.Constants;
import com.g2t2.util.Display;
import com.g2t2.util.StateFlags;
import com.g2t2.util.Utility;

import com.g2t2.types.ParadeBoard;
import com.g2t2.types.Player;
import com.g2t2.types.Card;

import com.g2t2.game.GameUtility.Prompt;

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
    private static int numOfPlayers = 0;
    private static int numOfCPUPlayers = 0;

    /**
     * Displays the game options either in console or GUI mode.
     * 
     * If GUI mode is enabled in {@link StateFlags}, it will display GUI options.
     * Otherwise, it will configure the game via console prompts.
     * 
     */
    public static void displayGameOptions() {
        if (StateFlags.isGUIMode) {
            // GUI display options
        } else {
            // Console configuration
            GameState game = new GameState();
            displayConsoleGameOptions(game);
            giveOutTheCards(game);
            GamePlay.startGame(game);
        }
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
        // Allow user to configure players, difficulty?
        Utility.clearConsoleScreen();
        System.out.println(Constants.BANNER);
        System.out.println(Constants.DIVIDER);
        
        numOfPlayers = GameInitPrompt.promptForNumberofPlayers();

        numOfCPUPlayers = GameInitPrompt.promptForCPUPlayerNumber(numOfPlayers);
        // choosing difficulty level
        for (int i = 0; i < numOfCPUPlayers; i++) {
            GameInitPrompt.promptForCPUDifficulty(game);
        }  
    }

    /**
     * Initializes the players and parade board for the game.
     * 
     * This method sets up human and CPU-controlled players, assigns names,
     * and distributes cards to create an initial parade board. It ensures that
     * all necessary components are ready before gameplay begins.
     *
     * @param game the current game state
     */
    private static void giveOutTheCards(GameState game) {
        Utility.clearConsoleScreen();
        System.out.println(Constants.BANNER);
        System.out.println(Constants.DIVIDER);
        for (int i = 1; i <= numOfPlayers - numOfCPUPlayers; i++) {
            GameInitPrompt.promptForPlayerName(game, i);
        }
        game.giveOutInitialCards();
    }
}
