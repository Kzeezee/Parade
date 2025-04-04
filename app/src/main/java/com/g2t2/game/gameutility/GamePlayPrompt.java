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
public class GamePlayPrompt {
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
                    if (confirmsChoice()) {
                        return Constants.LEAVE_GAME;
                    } else {
                        System.out.println();
                        continue;
                    }
                }

                int chosenCardIndex = Integer.parseInt(chosenCardString);

                if (chosenCardIndex < 1 || chosenCardIndex > numOfCards) {
                    throw new IndexOutOfBoundsException();
                }

                System.out.println("\t ➢ Chosen Card Number: " + (chosenCardIndex));
                if (!confirmsChoice()) {
                    System.out.println();
                    continue;
                }
                chosenCardString = chosenCardIndex - 1 + "";
                break;
            } catch (IllegalArgumentException e) {
                System.out.println(Display.errorColour(" ❌ Please input a number"));
                System.out.println();
            } catch (IndexOutOfBoundsException e) {
                System.out.println(Display.errorColour(" ❌ Please input a card number (1 to " + numOfCards + ")"));
                System.out.println();
            } catch (InputMismatchException e) {
                System.out.println();
            }
        }
        return chosenCardString;
    }

    public static boolean confirmsChoice() {
        Scanner sc = new Scanner(System.in);
        System.out.print(Display.colorString(" ⚜ Enter 'y' to confirm ➢ "));

        String confirmation = sc.nextLine();
        if (confirmation.length() == 1 && confirmation.toLowerCase().charAt(0) == 'y') {
            return true;
        }

        return false;
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
        GameRenderer.displayHandCards(player);

        boolean isReprompting = false;
        while (choosenCardsIdx[0].equals(choosenCardsIdx[1])) {
            if (isReprompting) {
                System.out.println(Display.errorColour(" ❌ Please choose 2 different cards!"));
                System.out.println();
            }
            for (int i = 0; i < 2; i++) {
                String prompt = " 📌 Which card do you want to place in the Collection?\n"
                        + " 📌 You need to choose TWO different cards \n"
                        + Display.colorString(" ⚜ Card " + (i + 1) + ": ");
                String cardIdx = promptForCard(player, prompt);

                if (cardIdx.equals(Constants.LEAVE_GAME) && confirmsChoice()) {
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
                return confirmation;
            } catch (InputMismatchException e) {
                System.out.println();
            }
        }
    }

}
