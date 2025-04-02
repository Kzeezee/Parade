package com.g2t2.game.gameutility;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import com.g2t2.game.GameState;
import com.g2t2.types.Player;

import com.g2t2.util.Constants;
import com.g2t2.util.Display;

/**
 * Utility class for handling user input prompts.
 * 
 * This class provides methods to interact with players during the game, such as
 * selecting cards, confirming actions, and choosing game options. It ensures
 * proper input validation and guides players through the game's decision-making
 * processes.
 * 
 * @author Shwe
 */
public class Prompt {
    /**
     * Options for selecting the difficulty level for CPU players.
     */
    public static final String GAME_DIFFICULTY_OPTIONS = " 📌 Choose the difficulty level for each of your CPU players\n"
            + //
            " 📌 Easy (1)\n" + //
            " 📌 Medium (2)\n" + //
            " 📌 Difficult (3)\n" + //
            Display.colorString(" ⚜ Enter option ➢ ");

    /**
     * Error message displayed when an invalid number of players is entered.
     */
    public static final String PLAYER_NO_ERROR_MESSAGE = Display
            .errorColour(" ❌ A minimum of 2 players and a maximum of 6 players are allowed."
                    + "Please enter a number between 2 to 6.");

    /**
     * Prompts the player to select a card from their hand.
     *
     * @param prompt The message displayed to the player.
     * @param player The player selecting a card.
     * @return The index of the selected card in the player's hand.
     */ 
    public static String promptForCard(Player player, String prompt) {
        String chosenCardString = null;
        Scanner sc = new Scanner(System.in);
        int numOfCards = player.getCardsOnHand().size();
        while (true) {
            try {
                System.out.println(prompt);
                System.out.print(
                        Display.colorString(" ⚜ Choose card number (1 to " + numOfCards + ") ➢ "));
                chosenCardString = sc.nextLine();

                if (chosenCardString.equals(Constants.LEAVE_GAME)) {
                    return Constants.LEAVE_GAME;
                }

                int chosenCardIndex = Integer.parseInt(chosenCardString);

                if (chosenCardIndex < 1 || chosenCardIndex > numOfCards) {
                    throw new IndexOutOfBoundsException();
                }

                // Confirmation on card choice
                System.out.println(Constants.ANSI_RESET + "      Chosen Card Number: " + (chosenCardIndex));
                System.out.print(Display.colorString(" ⚜ Enter 'y' to confirm ➢ "));

                String confirmation = sc.nextLine();
                if (confirmation.length() > 1 || !Character.isLetter(confirmation.charAt(0))
                        || Character.toLowerCase(confirmation.charAt(0)) != 'y') {
                    System.out.println();
                    continue;

                }
                chosenCardString = chosenCardIndex - 1 + "";
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(Display.errorColour(" ❌ Please input a number"));
                System.out.println();
            } catch (IndexOutOfBoundsException e) {
                System.out.println(Display.errorColour(" ❌ Please input a card number (1 to " + numOfCards + "): "));
                System.out.println();
            } catch (InputMismatchException e) {
                System.out.println();
            }
        }
        return chosenCardString;
    }

    /**
     * Prompts the player to select two cards from their hand to add to their
     * collection.
     *
     * @param player The player selecting cards.
     * @return An int[2] representing indices the selected cards from the player's
     *         hand.
     *         int[0] -> 1st card || int[1] -> 2nd card
     */
    public static String[] promptToCollectTwo(Player player) {
        String[] choosenCardsIdx = { "-1", "-1" };

        System.out.println(" ⚜ You need to select " + Display.colorString("TWO")
                + " cards from your hand to add to the collection.");
        System.out.println(Constants.DIVIDER);

        boolean isReprompting = false;
        while (choosenCardsIdx[0].equals(choosenCardsIdx[1])) {
            if (isReprompting) {
                System.out.println(Display.errorColour(" ❌ Please choose 2 different cards!"));
                System.out.println();
            }
            for (int i = 0; i < 2; i++) {
                String prompt = Display.colorString(" 📌 Which card do you want to place in the Collection?\n" +
                        " 📌 You need to choose TWO different cards \n" +
                        " ⚜ Card " + (i + 1) + ": ");
                String cardIdx = Prompt.promptForCard(player, prompt);
                if (cardIdx.equals(Constants.LEAVE_GAME)) {
                    choosenCardsIdx = new String[] { Constants.LEAVE_GAME };
                    break;
                }
                choosenCardsIdx[i] = cardIdx;
                System.out.println(Constants.DIVIDER);
            }
            isReprompting = true;
        }

        return choosenCardsIdx;
    }

    /**
     * Prompts the user to decide whether they want to start a new game after the
     * current game ends.
     */
    public static String promptForNewGame() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                System.out.println(" 📌 The game has ended. 📌 ");
                System.out.println(Display.colorString(" ⚜ Do you want to play another game? (y/n) ➢ "));
                String confirmation = sc.nextLine();
                System.out.println(Constants.ANSI_RESET);
                return confirmation;
            } catch (InputMismatchException e) {
                System.out.println();
            }
        }
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
        int numOfCPU = 0;
        String input = null;
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

        int difficulty = 0;
        while (true) {
            try {
                System.out.print(Constants.ANSI_RESET);
                System.out.println(Constants.DIVIDER);
                System.out.println(Constants.PROMPT_CANCEL_ACTION);
                System.out.print(Prompt.GAME_DIFFICULTY_OPTIONS);
                input = sc.nextLine();
                if (input.equals(Constants.CANCEL_ACTION)) {
                    return new String[] { input };
                }

                difficulty = Integer.parseInt(input);
                System.out.print(Constants.ANSI_RESET);
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
        return new String[] { "" + numOfCPU, "" + difficulty };
    }

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
                    return new String[] {input};
                }
                int numOfCPUToRemove = Integer.parseInt(input);
                if (numOfCPUToRemove < 1 || numOfCPUToRemove > currentNumOfCPU) {
                    System.out.println(Display.errorColour(" ❌ Please input a valid number. There are currently only " + currentNumOfCPU + " CPU(s)"));
                    continue;
                }

                // promptForCPUIdToRemove();

                int count = 0;
                while (count < numOfCPUToRemove) {
                    System.out.println(" 📌 Which CPU(s) do you want to remove? (Enter the playerId of the CPU)"); 
                    System.out.print(Display.colorString(" ➢ Enter CPU's playerId: "));
                    input = sc.nextLine();
                    if (input.equals(Constants.CANCEL_ACTION)) {
                        return new String[] {input};
                    }
                    int cpuId = Integer.parseInt(input);
                    if (game.checkPlayerIsCPU(cpuId)) {
                        idsToRemove.add(input);
                        count++;
                    } else {
                        System.out.println(Display.errorColour(" ❌ Invalid Player ID."));
                    }
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
