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
     * Options for selecting the difficulty level for CPU players.
     */
    public static final String GAME_DIFFICULTY_OPTIONS = " 📌 Choose the difficulty level for your CPU\n" 
                                                       + " 📌 Easy (1)\n" 
                                                       + " 📌 Medium (2)\n" 
                                                       + " 📌 Difficult (3)\n" 
                                                       + Display.colorString(" ⚜ Enter option ➢ ");

    /**
     * Error message displayed when an invalid number of players is entered.
     */
    public static final String PLAYER_NO_ERROR_MESSAGE = Display
            .errorColour(" ❌ A minimum of 2 players and a maximum of 6 players are allowed."
                        + "Please enter a number between 2 to 6.");


    /**
     * Prompts the user to select a difficulty level for CPU-controlled players.
     * Ensures that the chosen difficulty is within the allowed range.
     * 
     * @param game the {@link GameState} object to which the CPU player will be
     *             added
     */
    // public static void promptForCPUDifficulty(GameState game) {
    //     Scanner sc = new Scanner(System.in);
    //     int difficultyChosen = 0;
    //     while (true) {
    //         System.out.print(Constants.ANSI_RESET);
    //         System.out.println(Constants.DIVIDER);
    //         System.out.print(GAME_DIFFICULTY_OPTIONS);
    //         try {
    //             difficultyChosen = sc.nextInt();
    //             sc.nextLine();
    //             if (difficultyChosen < Constants.MIN_CPU_DIFFICULTY
    //                     || difficultyChosen > Constants.MAX_CPU_DIFFICULTY) {
    //                 throw new IllegalArgumentException(Display.errorColour(" ❌ Invalid CPU difficulty level chosen."));
    //             }
    //             break;
    //         } catch (InputMismatchException e) {
    //             System.out.println(Display.errorColour(" ❌ Invalid CPU difficulty level chosen."));
    //             sc.nextLine();
    //         } catch (IllegalArgumentException e) {
    //             System.out.println(e.getMessage());
    //         }

    //     }
    //     game.addPlayer(new Player(true, difficultyChosen));
    // }

    /**
     * Prompts the user to specify the total number of players for the game.
     * Ensures that the number of players is between 2 and 6.
     * 
     * @return the total number of players as an integer
     */
    // public static int promptForNumberofPlayers() {
    //     int numOfPlayers = 0;
    //     Scanner sc = new Scanner(System.in);
    //     while (true) {
    //         try {
    //             System.out.println(
    //                     " 📌 How many players are playing this game? (including any CPU-controlled players you want to be added)");
    //             System.out.print(Display.colorString(" ⚜ Enter the total number of players (2 to 6): "));
    //             numOfPlayers = sc.nextInt();
    //             sc.nextLine();
    //             if (numOfPlayers < 2 || numOfPlayers > 6) {
    //                 throw new IllegalArgumentException(PLAYER_NO_ERROR_MESSAGE);
    //             }
    //             break;
    //         } catch (InputMismatchException e) {
    //             System.out.println(Display.errorColour(" ❌ Please enter a valid number"));
    //             sc.nextLine();
    //         } catch (IllegalArgumentException e) {
    //             System.out.println(e.getMessage());
    //         }
    //     }
    //     return numOfPlayers;

    // }

    /**
     * Prompts the user to specify how many of the total players should be
     * CPU-controlled.
     * Ensures that there is at least one human player in the game.
     * 
     * @param numOfPlayers the total number of players in the game
     * @return the number of CPU-controlled players as an integer
     */
    // public static int promptForCPUPlayerNumber(int numOfPlayers) {
    //     int numOfCPU = 0;
    //     Scanner sc = new Scanner(System.in);
    //     while (true) {
    //         // prompts for no. of cpu players, ensuring min 1 human player
    //         try {
    //             System.out.println(" 📌 How many of these players do you want to be controlled by the CPU? ");
    //             System.out.print(Display
    //                     .colorString(" ⚜ Enter the total number of CPU players (0 to " + (numOfPlayers - 1) + "): "));
    //             numOfCPU = sc.nextInt();
    //             sc.nextLine();
    //             if (numOfCPU < 0 || numOfPlayers - numOfCPU < 1) {
    //                 throw new IllegalArgumentException(Display.errorColour(
    //                         " ❌ You must have a minimum of one human player. Please enter a number between 0 and "
    //                                 + (numOfPlayers - 1)));
    //             }
    //             break;
    //         } catch (InputMismatchException e) {
    //             System.out.println(Display.errorColour(" ❌ Please enter a valid number"));
    //             sc.nextLine();
    //         } catch (IllegalArgumentException e) {
    //             System.out.println(e.getMessage());
    //         }
    //     }
    //     return numOfCPU;
    // }

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
            System.out.println(Constants.PROMPT_CANCEL_ACTION);
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
                System.out.println(Constants.PROMPT_CANCEL_ACTION);
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
                System.out.println(Display.errorColour(" ❌ Please enter a number or an appropriate command"));
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
                System.out.println(Constants.PROMPT_CANCEL_ACTION);
                System.out.println(" 📌 How many CPU do you want to add?");
                System.out.print(Display.colorString(" ➢ Enter a number (max = " + maxNumOfCPU + "): "));
                input = sc.nextLine();
                if (input.equals(Constants.CANCEL_ACTION)) {
                    return new String[] { input };
                }
                numOfCPU = Integer.parseInt(input);
                if (numOfCPU < 0 || numOfCPU > maxNumOfCPU) {
                    throw new InputMismatchException(Display.errorColour(" ❌ Please enter a number between 0 to "
                            + maxNumOfCPU + ". Maximum number of players = " + Constants.MAX_PLAYER_NO));
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(Display.errorColour(" ❌ Please enter a number or an appropriate command"));
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
        List<String> idsToRemove = new ArrayList<>();
        int currentNumOfCPU = game.getNumOfCPU();
        while (true) {
            try {
                System.out.println(Constants.DIVIDER);
                System.out.println(Constants.PROMPT_CANCEL_ACTION);
                System.out.println(" 📌 How many CPU(s) do you want to remove?: ");
                System.out.print(Display.colorString(" ➢ Enter a number: "));
                input = sc.nextLine();
                if (input.equals(Constants.CANCEL_ACTION)) {
                    return new String[] { input };
                }
                int numOfCPUToRemove = Integer.parseInt(input);
                if (numOfCPUToRemove < 1 || numOfCPUToRemove > currentNumOfCPU) {
                    System.out.println(Display.errorColour(
                            " ❌ Please input a valid number. There are currently only " + currentNumOfCPU + " CPU(s)"));
                    continue;
                }

                int count = 0;
                List<Integer> checkInputs = new ArrayList<>();
                while (count < numOfCPUToRemove) {
                    System.out.println(" 📌 Which CPU(s) do you want to remove? (Enter the playerId of the CPU)");
                    System.out.print(Display.colorString(" ➢ Enter CPU's playerId: "));
                    input = sc.nextLine();
                    if (input.equals(Constants.CANCEL_ACTION)) {
                        return new String[] { input };
                    }
                    int cpuId = Integer.parseInt(input);
                    if (game.checkPlayerIsCPU(cpuId) && !checkInputs.contains(cpuId)) {
                        idsToRemove.add(input);
                        count++;
                    } else if (game.checkPlayerIsCPU(cpuId) && checkInputs.contains(cpuId)){
                        System.out.println(Display.errorColour(" ❌ Repeated ID. Select another Player ID."));
                    } else {
                        System.out.println(Display.errorColour(" ❌ Invalid Player ID."));
                    }
                    checkInputs.add(cpuId);
                    System.out.println();
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(Display.errorColour(" ❌ Please enter a number or an appropriate command"));
                System.out.println();
            }
        }
        idsToRemove.add(Constants.REMOVE_CPU);
        return idsToRemove.toArray(new String[0]);
    }

    public static String[] promptForPlayerRemoval(GameState game) {
        Scanner sc = new Scanner(System.in);
        String input = null;
        List<String> idsToRemove = new ArrayList<>();
        int currentNumOfPlayers = game.getNumOfPlayers();
        while (true) {
            try {
                System.out.println(Constants.DIVIDER);
                System.out.println(Constants.PROMPT_CANCEL_ACTION);
                System.out.println(" 📌 How many local player/CPU(s) do you want to remove?: ");
                System.out.print(Display.colorString(" ➢ Enter a number: "));
                input = sc.nextLine();
                if (input.equals(Constants.CANCEL_ACTION)) {
                    return new String[] { input };
                }
                int numOfPlayersToRemove = Integer.parseInt(input);
                if (numOfPlayersToRemove < 1 || numOfPlayersToRemove > currentNumOfPlayers) {
                    System.out.println(Display.errorColour(
                            " ❌ Please input a valid number. There are currently only " + currentNumOfPlayers + " player(s)"));
                    continue;
                }

                int count = 0;
                List<Integer> checkInputs = new ArrayList<>();
                while (count < numOfPlayersToRemove) {
                    System.out.println(" 📌 Which player do you want to remove? ");
                    System.out.print(Display.colorString(" ➢ Enter the playerId of ONE player: "));
                    input = sc.nextLine();
                    if (input.equals(Constants.CANCEL_ACTION)) {
                        return new String[] { input };
                    }
                    int playerID = Integer.parseInt(input);
                    if (game.findMyPlayer(playerID) != null && !checkInputs.contains(playerID)) {
                        idsToRemove.add(input);
                        count++;
                    } else if (game.findMyPlayer(playerID) != null && checkInputs.contains(playerID)){
                        System.out.println(Display.errorColour(" ❌ Repeated ID. Select another Player ID."));
                    } else {
                        System.out.println(Display.errorColour(" ❌ Invalid Player ID."));
                    }
                    checkInputs.add(playerID);
                    System.out.println();
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(Display.errorColour(" ❌ Please enter a number or an appropriate command"));
                System.out.println();
            }
        }
        idsToRemove.add(Constants.REMOVE_CPU);
        return idsToRemove.toArray(new String[0]);
    }
}
