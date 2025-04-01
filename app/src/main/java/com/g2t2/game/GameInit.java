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
    private static int difficulty;

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
        giveOutTheCards(game);
        GamePlay.startGame(new GameState());
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

        /* For local option */
        // - ben
        Scanner sc = new Scanner(System.in);
        numOfPlayers = 0;

        Utility.clearConsoleScreen();
        System.out.println(Constants.BANNER);
        System.out.println(Constants.DIVIDER);
        while (true) {
            // prompts for total players incl cpu
            try {
                System.out.println(
                        " 📌 How many players are playing this game? (including any CPU-controlled players you want to be added)");
                System.out.print(Display.colorString(" ⚜ Enter the total number of players (2 to 6): "));
                numOfPlayers = sc.nextInt();
                System.out.print(Constants.ANSI_RESET);
                if (numOfPlayers < 2 || numOfPlayers > 6) {
                    throw new IllegalArgumentException(Prompt.PLAYER_NO_ERROR_MESSAGE);
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println(Display.errorColour(" ❌ Please enter a number"));
                System.out.println();
                sc.nextLine();
            } catch (IllegalArgumentException e) {
                System.out.println(Display.errorColour(" ❌ Please enter a number"));
                System.out.println();
                sc.nextLine();
            }
        }
        while (true) {
            // prompts for no. of cpu players, ensuring min 1 human player
            try {
                System.out.println(" 📌 How many of these players do you want to be controlled by the CPU? ");
                System.out.print(Display.colorString(" ⚜ Enter the total number of CPU players: "));
                numOfCPU = sc.nextInt();
                System.out.print(Constants.ANSI_RESET);
                if (numOfCPU < 0 || numOfPlayers - numOfCPU < 1) {
                    throw new IllegalArgumentException(Display.errorColour(
                            " ❌ You must have a minimum of one human player. Please enter a number between 0 and "
                                    + (numOfPlayers - 1)));
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println(Display.errorColour(" ❌ Please enter a number"));
                System.out.println();
            } catch (IllegalArgumentException e) {
                System.out.println(Display.errorColour(" ❌ Please enter a number"));
            } finally {
                // Skips the enter key from nextInt();
                sc.nextLine();
            }
        }
        // choosing difficulty level
        if (numOfCPU > 0) {
            boolean validOptionSelected = false;
            int optionChosen = 0;
            while (!validOptionSelected) {
                System.out.print(Constants.ANSI_RESET);
                System.out.println(Constants.DIVIDER);
                System.out.print(Prompt.GAME_DIFFICULTY_OPTIONS);
                String s = sc.nextLine();
                System.out.print(Constants.ANSI_RESET);
                try {
                    optionChosen = Integer.parseInt(s);
                    switch (optionChosen) {
                        case 1:
                        case 2:
                        case 3:
                            validOptionSelected = true;
                            difficulty = optionChosen;
                            break;
                        default:
                            throw new Exception(Display.errorColour(" ❌ Invalid input"));
                    }
                } catch (NumberFormatException e) {
                    // Utility.clearConsoleScreen();
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    // Utility.clearConsoleScreen();
                    System.out.println(e.getMessage());
                }
            }
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
        Scanner sc = new Scanner(System.in);
        List<Player> players = new ArrayList<>();
        // Get first player name from player profile
        PlayerProfile pp = StateManager.getPlayerProfile();
        Player player = new Player(pp.getName());
        player.setPlayerProfile(pp);
        players.add(player); // User's player
        // Handle rest of the local players name input
        for (int i = 2; i <= numOfPlayers - numOfCPU; i++) {
            while (true) {
                try {
                    System.out.print(Display.colorString(" ➢ Enter the name of player " + i + ": "));
                    String name = sc.nextLine();
                    if (name.equals("")) {
                        throw new InputMismatchException(Display.errorColour(" 📌 Please enter an appropriate name."));
                    }
                    players.add(new Player(name));
                    break;
                } catch (InputMismatchException e) {
                    System.out.println(e.getMessage());
                    System.out.println();
                }
            }
        }

        // Modified to get player name from player profile
        // players.add(new Player(StateManager.getPlayerProfile().getName(),
        // ParadeBoard.getDECK()));

        for (int i = 1; i <= numOfCPU; i++) {
            String name = "CPU " + i;
            players.add(new Player(true, 1));
        }

        System.out.println(players);
        sc.nextLine();
        ParadeBoard.setPlayers(players);

        // Setting up the board
        List<Card> board = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            board.add(ParadeBoard.getDECK().drawCard());
        }
        ParadeBoard.setParade(board);
    }

    /**
     * Retrieves the difficulty level set for CPU-controlled players.
     *
     * @return The difficulty level as an integer value:
     *         1 - Easy
     *         2 - Medium
     *         3 - Hard
     */
    public static int getDifficulty() {
        return difficulty;
    }
}
