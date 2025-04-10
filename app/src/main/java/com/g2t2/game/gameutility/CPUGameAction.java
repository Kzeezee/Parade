package com.g2t2.game.gameutility;

import java.util.List;

import com.g2t2.game.GameState;
import com.g2t2.types.Card;
import com.g2t2.types.Player;
import com.g2t2.types.ScoreCalculation;
import com.g2t2.util.Constants;
import com.g2t2.util.Display;

/**
 * Utility class for handling CPU-specific game actions.a
 * 
 * This class provides methods for CPU players to execute their turns, select
 * cards
 * for collection, and make decisions based on difficulty levels. It is designed
 * to simulate the behavior of CPU players in the game.
 * 
 * @author Shw
 */
public class CPUGameAction {
    /**
     * Handles a CPU player's turn.
     *
     * @param player The CPU player.
     * @param game   The current game state.
     */
    public static String CPUSelectCard(Player player, GameState game) {
        System.out.println(Constants.DIVIDER);
        Display.delayPrint(" 📌 Player " + Display.colorString(player.getName())
                + " is choosing their cards.", 20);
        int cardIdx = chooseCPUCardIdx(player, game, -1);
        return String.valueOf(cardIdx);
    }

    /**
     * Allows a CPU player to select two cards from their hand for collection
     * purposes.
     *
     * @param player The CPU player selecting cards.
     * @param game   The current game state.
     * @return An int[2] of indices representing selected cards from the hand.
     */
    public static String[] CPUSelectTwoToCollections(Player player, GameState game) {
        int[] cardIdxList = {-1, -1};
        while (cardIdxList[0] == cardIdxList[1]) {
            for (int i = 0; i < cardIdxList.length; i++) {
                cardIdxList[i] = chooseCPUCardIdx(player, game, cardIdxList[0]);

            }
        }
        String[] returnCardIdxList = {String.valueOf(cardIdxList[0]), String.valueOf(cardIdxList[1])};
        return returnCardIdxList;
    }

    /**
     * Processes a list of selected cards and adds them to a player's collection.
     *
     * @param cardIdxList A list of indices representing selected cards from the
     *                    hand.
     * @param player      The player whose collection is being updated.
     */
    public static int chooseCPUCardIdx(Player player, GameState game, int idxOfFirstChosenCard) {
        int numOfCards = player.getCardsOnHand().size();
        switch (player.getDifficulty()) {
            // level 1 difficulty: random
            case 1:
                return (int) (Math.random() * numOfCards);
            // level 2 difficulty: picks the card that gives back the lowest number of cards
            // from the parade
            case 2:
                return chooseMediumDifficultyCard(player, game, idxOfFirstChosenCard);
            case 3:
                return chooseHardDifficultyCard(player, game, idxOfFirstChosenCard);
            default:
                return (int) (Math.random() * numOfCards);
        }
    }

    /**
     * Chooses a card for medium-level difficulty based on minimizing collectible
     * cards count.
     *
     * @param player The CPU player choosing a card.
     * @param game   The current game state.
     * @return An index representing the chosen card from the hand.
     */
    private static int chooseMediumDifficultyCard(Player player, GameState game, int idxOfFirstChosenCard) {
        List<Card> hand = player.getCardsOnHand();
        int lowestNumCollect = Integer.MAX_VALUE;
        int bestIdx = -1;

        for (int i = 0; i < hand.size(); i++) {
            if (i == idxOfFirstChosenCard) {
                continue;
            }
            List<Card> collectableCards = GameProcessor.getCollectableCards(hand.get(i), game);
            if (collectableCards.size() < lowestNumCollect) {
                lowestNumCollect = collectableCards.size();
                bestIdx = i;
            }
        }
        return bestIdx;
    }

    /**
     * Chooses a card for hard-level difficulty based on minimizing total points of
     * collectible cards.
     *
     * @param player The CPU player choosing a card.
     * @param game   The current game state.
     * @return An index representing the chosen card from the hand.
     */
    private static int chooseHardDifficultyCard(Player player, GameState game, int idxOfFirstChosenCard) {
        List<Card> hand = player.getCardsOnHand();
        int lowestPoints = Integer.MAX_VALUE;
        int bestIdx = -1;

        for (int i = 0; i < hand.size(); i++) {
            if (i ==idxOfFirstChosenCard) {
                continue;
            }
            List<Card> collectableCards = GameProcessor.getCollectableCards(hand.get(i), game);
            int totalPoints = ScoreCalculation.calculateTotalPoints(collectableCards);

            if (totalPoints < lowestPoints) {
                lowestPoints = totalPoints;
                bestIdx = i;
            }
        }
        return bestIdx;
    }

    /**
     * Display to the client what the CPU is currently doing during their turn 
     * in an online game
     * 
     * @param player name of the current CPU player
     */
    public static void displayCPUTurnAction(Player player) {
        Display.delayPrint(" 📌 Player " + Display.colorString(player.getName())
                + " is choosing their cards.", 20);
    }
}

