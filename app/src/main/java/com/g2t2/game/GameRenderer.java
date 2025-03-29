package com.g2t2.game;

import java.util.Comparator;
import java.util.List;

import com.g2t2.types.Player;
import com.g2t2.util.Constants;
import com.g2t2.util.Display;

public class GameRenderer {
    public static void displayRoundInformation(GameState game) {
        Display.printGameHeader();
        displayCollections(game);
        displayParade(game);
        game.printNumOfCardsLeft();
        game.printPlayerTurn();
        if (game.isLastRound()) {
            System.out.println(" ⚜ " + Display.colorString(" 📌 LAST ROUND 📌 ") + Constants.ANSI_RESET);
        }
        System.out.println(Constants.DIVIDER);
    }

    // show collections of all players
    public static void displayCollections(GameState game) {
        for (Player p : game.getPlayers()) {
            System.out.println(
                    Display.colorString("「 ✦ CARDS IN " + p.getName() + "(" + p.getPlayerId() + ") COLLECTION ✦ ")
                            + Constants.ANSI_RESET);
            Display.displayAllStacks(p.getCardsInCollection());
        }
    }

    // show the parade collection
    public static void displayParade(GameState game) {
        System.out.println(Display.colorString("「 ✦ PARADE COLLECTION ✦ 」" + Constants.ANSI_RESET));
        System.out.println("The front of the Parade starts from the left");
        Display.displayCardsSideBySide(game.getParade());
        System.out.println(Constants.DIVIDER);
    }

    // show the player what card he has in their hand
    public static void displayHandCards(Player player) {
        System.out.println(Display.colorString("「 ✦ CARDS IN YOUR HAND ✦ 」") + Constants.ANSI_RESET);
        System.out.println("Each card is numbered, starting from the left, 1");
        Display.displayCardsSideBySide(player.getCardsOnHand());
        System.out.println(Constants.DIVIDER);
    }

    // display the scores of the players and rank them in ascending order
    public static void displayScores(List<Player> players) {
        System.out.println(Constants.DIVIDER);
        System.out.println(Display.colorString("「 ✦ RANKING ✦ 」") + Constants.ANSI_RESET);

        // Sort players by score
        List<Player> sortedPlayers = players.stream()
                .sorted(Comparator.comparingInt(Player::getScore))
                .toList();

        // Assign ranks based on sorted order
        int rank = 1;
        for (Player player : sortedPlayers) {
            System.out.printf("%d. Player %d, %s, Score: [%d]%n", rank, player.getPlayerId(), player.getName(),
                    player.getScore());
            rank++;
        }
    }
}
