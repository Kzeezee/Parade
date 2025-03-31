package com.g2t2.game;

import java.util.InputMismatchException;
import java.util.Scanner;

import com.g2t2.game.GameUtility.Prompt;
import com.g2t2.types.Player;
import com.g2t2.util.Constants;
import com.g2t2.util.Display;

/**
 * Handles user input prompts during the initialization phase of the game.
 * Provides methods for configuring CPU difficulty, determining the number of
 * players,
 * assigning CPU-controlled players, and setting player names.
 * 
 * @author Shwe
 */
public class GameInitPrompt {
    /**
     * Prompts the user to select a difficulty level for CPU-controlled players.
     * Ensures that the chosen difficulty is within the allowed range.
     * 
     * @param game the {@link GameState} object to which the CPU player will be
     *             added
     */
    public static void promptForCPUDifficulty(GameState game) {
        Scanner sc = new Scanner(System.in);
        int difficultyChosen = 0;
        while (true) {
            System.out.print(Constants.ANSI_RESET);
            System.out.println(Constants.DIVIDER);
            System.out.print(Prompt.GAME_DIFFICULTY_OPTIONS);
            try {
                difficultyChosen = sc.nextInt();
                sc.nextLine();
                if (difficultyChosen < Constants.MIN_CPU_DIFFICULTY
                        || difficultyChosen > Constants.MAX_CPU_DIFFICULTY) {
                    throw new IllegalArgumentException(Display.errorColour(" ❌ Invalid CPU difficulty level chosen."));
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println(Display.errorColour(" ❌ Invalid CPU difficulty level chosen."));
                sc.nextLine();
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }

        }
        game.addPlayer(new Player(true, difficultyChosen));
    }

    /**
     * Prompts the user to specify the total number of players for the game.
     * Ensures that the number of players is between 2 and 6.
     * 
     * @return the total number of players as an integer
     */
    public static int promptForNumberofPlayers() {
        int numOfPlayers = 0;
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                System.out.println(
                        " 📌 How many players are playing this game? (including any CPU-controlled players you want to be added)");
                System.out.print(Display.colorString(" ⚜ Enter the total number of players (2 to 6): "));
                numOfPlayers = sc.nextInt();
                sc.nextLine();
                if (numOfPlayers < 2 || numOfPlayers > 6) {
                    throw new IllegalArgumentException(Prompt.PLAYER_NO_ERROR_MESSAGE);
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println(Display.errorColour(" ❌ Please enter a valid number"));
                sc.nextLine();
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
        return numOfPlayers;

    }

    /**
     * Prompts the user to specify how many of the total players should be
     * CPU-controlled.
     * Ensures that there is at least one human player in the game.
     * 
     * @param numOfPlayers the total number of players in the game
     * @return the number of CPU-controlled players as an integer
     */
    public static int promptForCPUPlayerNumber(int numOfPlayers) {
        int numOfCPU = 0;
        Scanner sc = new Scanner(System.in);
        while (true) {
            // prompts for no. of cpu players, ensuring min 1 human player
            try {
                System.out.println(" 📌 How many of these players do you want to be controlled by the CPU? ");
                System.out.print(Display
                        .colorString(" ⚜ Enter the total number of CPU players (0 to " + (numOfPlayers - 1) + "): "));
                numOfCPU = sc.nextInt();
                sc.nextLine();
                if (numOfCPU < 0 || numOfPlayers - numOfCPU < 1) {
                    throw new IllegalArgumentException(Display.errorColour(
                            " ❌ You must have a minimum of one human player. Please enter a number between 0 and "
                                    + (numOfPlayers - 1)));
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println(Display.errorColour(" ❌ Please enter a valid number"));
                sc.nextLine();
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
        return numOfCPU;
    }

    /**
     * Prompts the user to enter a name for a specific player.
     * Ensures that the name is not empty and adds the player to the game state.
     * 
     * @param game the {@link GameState} object to which the player will be added
     * @param idx  the index of the player being named, used for display purposes
     */
    public static void promptForPlayerName(GameState game, int idx) {
        Scanner sc = new Scanner(System.in);
        String name = "";
        while (true) {
            try {
                System.out.print(Display.colorString(" ➢ Enter the name of player " + idx + ": "));
                name = sc.nextLine();
                if (name.equals("")) {
                    throw new InputMismatchException(Display.errorColour(" 📌 Please enter an appropriate name."));
                }
                // players.add(new Player(name));
                game.addPlayer(new Player(name));
                break;
            } catch (InputMismatchException e) {
                System.out.println(e.getMessage());
                sc.nextLine();
            }
        }
    }
}
