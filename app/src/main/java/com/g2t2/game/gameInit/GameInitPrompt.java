package com.g2t2.game.gameInit;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import com.g2t2.game.GameState;
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
     * Prompts the user to enter a name for a specific player.
     * Ensures that the name is not empty and adds the player to the game state.
     * 
     * @param game the {@link GameState} object to which the player will be added
     * @param idx  the index of the player being named, used for display purposes
     */
    public static String promptForPlayerName() {
        Scanner sc = new Scanner(System.in);
        String name = "";
        while (true) {
            System.out.println(Constants.DIVIDER);
            System.out.println(Constants.CANCEL_ACTION_PROMPT);
            System.out.print(Display.colorString(" ➢ Enter the name of player: "));
            name = sc.nextLine();
            if (name.equals(Constants.CANCEL_ACTION)) {
                return name;
            }
            if (name.length() < 1) {
                System.out.println(Display.errorColour(" ❌ Please enter something as your name."));
                System.out.println();
                continue;
            }
            break;
        }
        return name;
    }

    /**
     * Prompts the player for the details of the CPUs that they want to add.
     * Details contains: "number of CPUs" and "Difficulty Level"
     * 
     * @param numOfPlayers Number of players currently playing the game
     * @return A String array that represents user's selection of the CPU
     *         information.
     *         Index 0 -> Number of CPU to add
     *         Index 1 -> Difficulty Level
     */
    public static String[] promptForCPUDetails(int numOfPlayers) {
        Scanner sc = new Scanner(System.in);
        int maxNumOfCPU = Constants.MAX_PLAYER_NO - numOfPlayers;
        String input = null;
        int difficulty = 0;
        while (true) {
            try {
                System.out.println(Constants.DIVIDER);
                System.out.println(Constants.CANCEL_ACTION_PROMPT);
                System.out.print(GAME_DIFFICULTY_OPTIONS);
                input = sc.nextLine();
                if (input.equals(Constants.CANCEL_ACTION)) {
                    return new String[] {input};
                }

                difficulty = Integer.parseInt(input);
                if (difficulty < Constants.MIN_CPU_DIFFICULTY || difficulty > Constants.MAX_CPU_DIFFICULTY) {
                    throw new InputMismatchException(Display.errorColour(" ❌ Please enter a number between " +
                            Constants.MIN_CPU_DIFFICULTY + " to " + Constants.MAX_CPU_DIFFICULTY));
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(Constants.INVALID_OPTION_ERROR_MSG);
                System.out.println();
            } catch (InputMismatchException e) {
                System.out.println(e.getMessage());
                System.out.println();
            }
        }

        int numOfCPU = 0;
        while (true) {
            try {
                System.out.println(Constants.DIVIDER);
                System.out.println(Constants.CANCEL_ACTION_PROMPT);
                System.out.println(" 📌 How many CPU do you want to add?");
                System.out.print(Display.colorString(" ➢ Enter a number (max = " + maxNumOfCPU + "): "));
                input = sc.nextLine();
                if (input.equals(Constants.CANCEL_ACTION)) {
                    return new String[] {input};
                }
                numOfCPU = Integer.parseInt(input);
                if (numOfCPU < 0 || numOfCPU > maxNumOfCPU) {
                    throw new InputMismatchException(Display.errorColour(" ❌ Please enter a number between 0 to "
                            + maxNumOfCPU + ". Maximum number of players = " + Constants.MAX_PLAYER_NO));
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(Constants.INVALID_OPTION_ERROR_MSG);
                System.out.println();
            } catch (InputMismatchException e) {
                System.out.println(e.getMessage());
                System.out.println();
            }
        }

        return new String[] { "" + numOfCPU, "" + difficulty };
    }

    /**
     * Prompts the user to select CPU players to remove from the game.
     * 
     * This method handles the process of removing CPU players by:
     * 1. Asking the user how many CPU players they want to remove
     * 2. Validating that the number is between 1 and the current number of CPUs
     * 3. Prompting the user to specify the player IDs of the CPUs to remove
     * 4. Validating that each ID belongs to a CPU player
     * 
     * @param game the current game state containing CPU players
     * @return a String array containing the IDs of CPUs to remove, or an array with
     *         a single element containing the cancel action if the user cancels
     */
    public static String[] promptForCPURemoval(GameState game) {
        Scanner sc = new Scanner(System.in);
        String input = null;
        int currentNumOfCPU = game.getNumOfCPU();
        List<String> idsToRemove = null;
        while (true) {
            try {
                System.out.println(Constants.DIVIDER);
                System.out.println(Constants.CANCEL_ACTION_PROMPT);
                System.out.println(" 📌 How many CPU(s) do you want to remove?: ");
                System.out.print(Display.colorString(" ➢ Enter a number: "));
                input = sc.nextLine();
                if (input.equals(Constants.CANCEL_ACTION)) {
                    return new String[] {input};
                }
                int numOfCPUToRemove = Integer.parseInt(input);
                if (numOfCPUToRemove < 1 || numOfCPUToRemove > currentNumOfCPU) {
                    System.out.println(Display.errorColour(
                            " ❌ Please input a valid number. There are currently only " + currentNumOfCPU + " CPU(s)"));
                    continue;
                }
                System.out.println(Constants.DIVIDER);

                int count = 0;
                idsToRemove = new ArrayList<>();
                while (count < numOfCPUToRemove) {
                    System.out.println(Constants.CANCEL_ACTION_PROMPT);
                    System.out.println(" 📌 Which CPU(s) do you want to remove? (Enter the playerId of the CPU)");
                    System.out.print(Display.colorString(" ➢ Enter CPU's playerId (" + (count+1) +"): "));
                    input = sc.nextLine();
                    if (input.equals(Constants.CANCEL_ACTION)) {
                        return new String[] {input};
                    }
                    if (isPlayerIdValidForRemoval(input, idsToRemove, game, true)) {
                        count++;
                    }
                    System.out.println();
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(Constants.INVALID_OPTION_ERROR_MSG);
                System.out.println();
            }
        }
        idsToRemove.add(Constants.REMOVE_CPU);
        return idsToRemove.toArray(new String[0]);
    }

    public static String[] promptForPlayerRemoval(GameState game) {
        Scanner sc = new Scanner(System.in);
        String input = null;
        int currentNumOfPlayers = game.getNumOfPlayers();
        List<String> idsToRemove = null;
        while (true) {
            try {
                System.out.println(Constants.DIVIDER);
                System.out.println(Constants.CANCEL_ACTION_PROMPT);
                System.out.println(" 📌 How many local player/CPU(s) do you want to remove?: ");
                System.out.print(Display.colorString(" ➢ Enter a number: "));
                input = sc.nextLine();
                if (input.equals(Constants.CANCEL_ACTION)) {
                    return new String[] {input};
                }
                int numOfPlayersToRemove = Integer.parseInt(input);
                if (numOfPlayersToRemove < 1 || numOfPlayersToRemove > currentNumOfPlayers) {
                    System.out.println(Display.errorColour(
                            " ❌ Please input a valid number. There are currently only " + currentNumOfPlayers + " player(s)"));
                    continue;
                }
                System.out.println(Constants.DIVIDER);

                int count = 0;
                idsToRemove = new ArrayList<>();
                while (count < numOfPlayersToRemove) {
                    System.out.println(Constants.CANCEL_ACTION_PROMPT);
                    System.out.println(" 📌 Which player do you want to remove? (Enter the playerId of the Player)");
                    System.out.print(Display.colorString(" ➢ Enter Player's playerID (" + (count+1) + "): "));
                    input = sc.nextLine();
                    if (input.equals(Constants.CANCEL_ACTION)) {
                        return new String[] {input};
                    }
                    if (isPlayerIdValidForRemoval(input, idsToRemove, game, false)) {
                        count++;
                    }
                    System.out.println();
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(Constants.INVALID_OPTION_ERROR_MSG);
                System.out.println();
            }
        }
        idsToRemove.add(Constants.REMOVE_CPU);
        return idsToRemove.toArray(new String[0]);
    }

    /**
     * Checks if the playerID inputted by the user is valid for removal
     * 1. It checks if that playerID exist in the game state
     * 2. It checks if it is a repeated ID that was already inputted
     * 
     * @param input user's input
     * @param idsToRemove a list of playerIDs representing user's current choice of players to remove
     * @param game the current game state
     * @param isOnlyRemovingCPU flag indicating whether the removal is only for CPU or for all players (to get the correct list to valid playerID)
     * @return true = valid playerID for removal and will be added to the list of IDs to remove, false means otherwise.
     * @throws IllegalArgumentException this happens when the user does not input a number / the LEAVE GAME command
     */
    private static boolean isPlayerIdValidForRemoval(String input, List<String> idsToRemove, GameState game, boolean isOnlyRemovingCPU) throws IllegalArgumentException {
        int playerID = Integer.parseInt(input);
        boolean conditionToCheck = isOnlyRemovingCPU ? game.checkPlayerIsCPU(playerID) : game.findMyPlayer(playerID) != null;
        if (idsToRemove.contains(input)) {
            System.out.println(Display.errorColour(" ❌ Repeated ID. Select another playerID."));
        } else if (conditionToCheck) {
            idsToRemove.add(input);
            return true;
        } else {
            System.out.println(Display.errorColour(" ❌ Invalid playerID."));
        }
        return false;
    }

    /**
     * Options for selecting the difficulty level for CPU players.
     */
    private static final String GAME_DIFFICULTY_OPTIONS = " 📌 Choose the difficulty level for your CPU\n" 
                                                       + " 📌 Easy (1)\n" 
                                                       + " 📌 Medium (2)\n" 
                                                       + " 📌 Difficult (3)\n" 
                                                       + Display.colorString(" ⚜ Enter option ➢ ");
}
