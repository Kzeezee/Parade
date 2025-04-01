package com.g2t2.game.gameutility;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import com.g2t2.game.GameState;
import com.g2t2.game.StartMenu;
import com.g2t2.game.GamePlay;

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
    public static final String GAME_DIFFICULTY_OPTIONS = " 📌 Choose the difficulty level for your CPU players\n" + //
            " 📌 Easy (1)\n" + //
            " 📌 Medium (2)\n" + //
            " 📌 Difficult (3)\n" + //
            Display.colorString(" ⚜ Enter option ➢ ");

    /**
     * Options for selecting the local game mode.
     */
    public static final String LOCAL_MODE_OPTIONS = " 📌 Please select an option.\n"
            + " 📌 Play with friends (1)\n"
            + " 📌 Play with CPU (2)\n"
            + Display.colorString(" ⚜ Enter option ➢ ");

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
    public static int promptForCard(String prompt, Player player) {
        int chosenCardIndex = 0;
        Scanner sc = new Scanner(System.in);
        int numOfCards = player.getCardsOnHand().size();
        while (true) {
            try {
                System.out.println(prompt);
                System.out.print(
                        Display.colorString(" ⚜ Choose card number (1 to " + numOfCards + ") or 'q' to quit ➢ "));
                String chosenCardString = sc.nextLine();
                if (chosenCardString.equalsIgnoreCase("q")) {
                    Display.sayGoodBye();
                    System.exit(0);
                }
                chosenCardIndex = Integer.parseInt(chosenCardString);

                if (chosenCardIndex < 1 || chosenCardIndex > numOfCards) {
                    throw new IndexOutOfBoundsException();
                }

                // Confirmation on card choice
                System.out.println(Constants.ANSI_RESET + " 📌 Chosen Card Number: " + (chosenCardIndex));
                System.out.print(Display.colorString(" ⚜ Enter 'y' to confirm or 'q' to quit ➢ "));

                String confirmation = sc.nextLine();
                if (confirmation.length() > 1 || !Character.isLetter(confirmation.charAt(0))
                        || Character.toLowerCase(confirmation.charAt(0)) != 'y') {
                    System.out.println();
                    continue;

                }
                break;
            } catch (InputMismatchException e) {
                System.out.println(Display.errorColour(" ❌ Please input a number"));
                System.out.println();
                sc.nextLine();
            } catch (IndexOutOfBoundsException e) {
                System.out.println(Display.errorColour(" ❌ Please input a card number (1 to " + numOfCards + "): "));
                System.out.println();
            } catch (IllegalArgumentException e) {
                System.out.println();
            }
        }
        return chosenCardIndex - 1;
    }

    /**
     * Prompts the player to select two cards from their hand to add to their
     * collection.
     *
     * @param player The player selecting cards.
     * @return An int[2] representing indices the selected cards from the player's hand.
     *         int[0] -> 1st card || int[1] -> 2nd card
     */
    public static int[] promptToCollectTwo(Player player) {
        int[] choosenCardsIdx = {-1 , -1};

        System.out.println(" ⚜ You need to select " + Display.colorString("TWO")
                + " cards from your hand to add to the collection.");
        System.out.println(Constants.DIVIDER);

        // addSelectedCardsToParade(player);
        boolean isReprompting = false;
        while (choosenCardsIdx[0] == choosenCardsIdx[1]) {
            if (isReprompting) {
                System.out.println(Display.errorColour(" ❌ Please choose 2 different cards!"));
                System.out.println();
            }
            for (int i = 0; i < 2; i++) {
                String prompt = Display.colorString(" 📌 Which card do you want to place in the Collection?\n" +
                        " 📌 You need to choose TWO different cards \n" +
                        " ⚜ Card " + (i + 1) + ": ");
                int cardIdx = Prompt.promptForCard(prompt, player);
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
    public static void promptForNewGame() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                System.out.println(" 📌 The game has ended. 📌 ");
                System.out.println(Display.colorString(" ⚜ Do you want to play another game? (y/n) ➢ "));
                String confirmation = sc.nextLine();

                if (confirmation.equalsIgnoreCase("y")) {
                    StartMenu.showStartMenu();
                    GamePlay.startGame(new GameState());
                } else if (confirmation.equalsIgnoreCase("n")) {
                    Display.sayGoodBye();
                    System.exit(0);
                }
                System.out.println(Constants.ANSI_RESET);
            } catch (IllegalArgumentException e) {
                System.out.println();
            }
        }
    }

    /**
     * Prompts the player for the details of the CPUs that they want to add.
     * Details contains: "number of CPUs" and "Difficulty Level"
     * 
     * @param numOfPlayers Number of players currently playing the game
     * @return A String array that represents user's selection of the CPU information.
     *         Index 0 -> Number of CPU to add
     *         Index 1 -> Difficulty Level
     */ // MAY CONVERT TO INT[]
    public static String[] promptForCPUDetails(int numOfPlayers) {
        Scanner sc = new Scanner(System.in);
        int maxNumOfCPU = 6 - numOfPlayers;
        int numOfCPU = 0;
        while (true) {
            try {        
                System.out.println(" 📌 How many CPU do you want to add?"); 
                System.out.print(Display.colorString(" ➢ Enter a number (max = " + maxNumOfCPU + "): "));
                numOfCPU = sc.nextInt();
                if (numOfCPU < 0 || numOfCPU > maxNumOfCPU) {
                    throw new IllegalArgumentException(Display.errorColour(" ❌ Please enter a number between 0 to " 
                    + maxNumOfCPU + ". Maximum number of players = 6"));
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println(Display.errorColour(" ❌ Please enter a number"));
                System.out.println();
                sc.nextLine();
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                System.out.println();
            }
        }

        if (numOfCPU == 0) {
            return null;
        }
        
        int difficulty = 0;
        while (true) {
            try {
                System.out.print(Constants.ANSI_RESET);
                System.out.println(Constants.DIVIDER);
                System.out.println(Prompt.GAME_DIFFICULTY_OPTIONS);
                difficulty = sc.nextInt();
                System.out.print(Constants.ANSI_RESET);
                if (difficulty < 1 || difficulty > 3) {
                    throw new IllegalArgumentException(Display.errorColour(" ❌ Please enter a number between 1 to 3"));
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println(Display.errorColour(" ❌ Please enter a number"));
                System.out.println();
                sc.nextLine();
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                System.out.println();
            }
        }
        return new String[] {"" + numOfCPU, "" + difficulty};
    }
}
