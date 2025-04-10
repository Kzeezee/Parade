package com.g2t2.game.gameutility;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import com.g2t2.game.GameState;
import com.g2t2.types.Player;
import com.g2t2.util.Constants;
import com.g2t2.util.Display;
import com.g2t2.util.Utility;

/**
 * Utility class for rendering game information to the console.
 * 
 * This class provides methods to display various aspects of the game state,
 * including round information, player collections, parade details, hand cards,
 * and player scores. It is primarily responsible for presenting data in a
 * user-friendly format during gameplay.
 * 
 * @author Ben
 * @author Shwe
 */
public class GameRenderer {
    /**
     * Displays general information about the current round.
     * Includes collections, parade details, number of cards left, and whose turn it
     * is.
     *
     * @param game the current game state
     */
    public static void displayRoundInformation(GameState game) { // show everything that everyone can see (no player's
                                                                 // hand)
        
        Utility.clearConsoleScreen();
        GameRenderer.gameHeader();
        displayCollections(game);
        if (!game.isPut2CollectionRound()){
            displayParade(game);
        }
        game.printNumOfCardsLeft();
        game.printPlayerTurn();
        if (game.isPut2CollectionRound()) {
            System.out.println(Display.colorString(" 📌 The collections above are the collections after the final round"));
            System.out.println(" ⚜ " + Display.colorString(" 📌 PUT 2 COLLECTION ROUND 📌 "));
        } else if (game.isLastRound()) {
            System.out.println(" ⚜ " + Display.colorString(" 📌 LAST ROUND 📌 ")); // IS THE STAR AT THE START NECESSARY
        } 
        System.out.println(Constants.DIVIDER);
    }

    /**
     * Displays the collections of all players in the game.
     * Displays frozen state during put 2 to collection round
     *
     * @param game the current game state containing player information
     */
    public static void displayCollections(GameState game) {
        for (Player p : game.getPlayers()) {
            System.out.println(
                    Display.colorString("「 ✦ CARDS IN " + p.getName() + "(" + p.getPlayerId() + ") COLLECTION ✦ 」"));
            if (game.isPut2CollectionRound()) {
                Display.displayAllStacks(p.getFinalCardsInCollection());
            } else { // normal round
                Display.displayAllStacks(p.getCardsInCollection());
            }
        }
    }

    /**
     * Displays the parade collection, showing all cards currently in the parade.
     *
     * @param game the current game state containing parade information
     */
    public static void displayParade(GameState game) {
        System.out.println(Display.colorString("「 ✦ PARADE COLLECTION ✦ 」" + Constants.ANSI_RESET));
        System.out.println("The front of the Parade starts from the left");
        Display.displayCardsSideBySide(game.getParade(), false);
        System.out.println(Constants.DIVIDER);
    }

    /**
     * Displays the cards in a player's hand.
     *
     * @param player the player whose hand cards are being displayed
     */
    public static void displayHandCards(Player player) {
        System.out.println(Display.colorString("「 ✦ CARDS IN YOUR HAND ✦ 」") + Constants.ANSI_RESET);
        System.out.println("Each card is numbered, starting from the left, 1");
        Display.displayCardsSideBySide(player.getCardsOnHand(), true);
        System.out.println(Constants.DIVIDER);
    }

    /**
     * Displays the scores of all players and ranks them in ascending order.
     *
     * @param players a list of players whose scores are to be displayed and ranked
     */
    public static void displayScores(List<Player> players) {
        // System.out.println(Constants.DIVIDER);
        System.out.println(Display.colorString("「 ✦ RANKING ✦ 」") + Constants.ANSI_RESET);

        // Sort players by score
        List<Player> sortedPlayers = players.stream()
                .sorted(Comparator.comparingInt(Player::getScore))
                .toList();

        // Assign ranks based on sorted order
        int rank = 1;
        for (Player player : sortedPlayers) {
            Display.delayPrint(String.format("(%d) Player %d, %s, Score: [%d], Num of Cards in Collection: [%d]%n", 
            rank, player.getPlayerId(), player.getName(), player.getScore(), player.getCollectionSize()), 20);
            rank++;
        }
    }

        /**
     * Creates a temporary pause in the game flow, waiting for user input to continue.
     *
     * @param message The message to display to the user during the pause
     */
    public static void temporaryPauseScreen(String message) {
        Scanner sc = new Scanner(System.in);
        System.out.print(Display.colorString(message));
        sc.nextLine();
    }

    /**
     * Prints the game header.
     */
    public static void gameHeader() {
        Utility.clearConsoleScreen();
        System.out.println(Constants.BANNER);
        System.out.println(Constants.LEAVE_GAME_PROMPT);
        System.out.println(Constants.DIVIDER);
    }
}
