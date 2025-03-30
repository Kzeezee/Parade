package com.g2t2.game.GameUtility;

import java.util.ArrayList;
import java.util.List;

import com.g2t2.game.GameState;
import com.g2t2.types.Card;
import com.g2t2.types.Player;
import com.g2t2.types.ScoreCalculation;
import com.g2t2.util.Constants;
import com.g2t2.util.Display;

/**
 * Utility class for handling CPU-specific game actions.
 * 
 * This class provides methods for CPU players to execute their turns, select
 * cards
 * for collection, and make decisions based on difficulty levels. It is designed
 * to simulate the behavior of CPU players in the game.
 * 
 * @author Shwe
 * @author Nathan
 */
public class CPUGameAction {
    /**
     * Handles a CPU player's turn.
     *
     * @param player the CPU player
     * @param game   the current game state
     */
    protected static void CPUplayTurn(Player player, GameState game) {
        System.out.println(Constants.DIVIDER);
        Display.delayPrint(" 📌 Player " + Display.colorString(player.getName())
                + " is choosing their cards.", 30);
        int cardIdx = chooseCPUCardIdx(player, game);
        GameProcessor.processCardChosenToPutToParade(cardIdx, player, game);
    }

    /**
     * Allows a CPU player to select two cards from their hand for collection
     * purposes.
     *
     * @param player the CPU player selecting cards
     * @param game   the current game state
     * @return a list of indices representing selected cards from the hand
     */
    protected static List<Integer> CPUSelectTwoToCollections(Player player, GameState game) {
        List<Integer> cardIdxList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            int idx = chooseCPUCardIdx(player, game);
            cardIdxList.add(idx);
        }
        return cardIdxList;
    }

    /**
     * Processes a list of selected cards and adds them to a player's collection.
     *
     * @param cardIdxList a list of indices representing selected cards from the
     *                    hand
     * @param player      the player whose collection is being updated
     */
    private static int chooseCPUCardIdx(Player player, GameState game) {
        int numOfCards = player.getCardsOnHand().size();
        switch (player.getDifficulty()) {
            // level 1 difficulty: random
            case 1:
                return (int) (Math.random() * numOfCards);
            // level 2 difficulty: picks the card that gives back the lowest number of cards
            // from the parade
            case 2:
                return chooseMediumDifficultyCard(player, game);
            // level 3 difficulty: picks the card that gives back the lowest sum of values of
            // the cards from the parade
            case 3:
                return chooseHardDifficultyCard(player, game);
            default:
                return (int) (Math.random() * numOfCards);
        }
    }

    /**
     * Chooses a card for medium-level difficulty based on minimizing collectible
     * cards count.
     *
     * @param player the CPU player choosing a card
     * @param game   the current game state
     * @return an index representing the chosen card from the hand
     */
    private static int chooseMediumDifficultyCard(Player player, GameState game) {
        List<Card> hand = player.getCardsOnHand();
        int lowestNumCollect = Integer.MAX_VALUE;
        // Card bestCard = player.getCardsOnHand().get(0);
        int bestIdx = -1;

        for (int i = 0; i < hand.size(); i++) {
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
     * @param player the CPU player choosing a card
     * @param game   the current game state
     * @return an index representing the chosen card from the hand
     */
    private static int chooseHardDifficultyCard(Player player, GameState game) {
        List<Card> hand = player.getCardsOnHand();
        int lowestPoints = Integer.MAX_VALUE;
        int bestIdx = -1;

        for (int i = 0; i < hand.size(); i++) {
            List<Card> collectableCards = GameProcessor.getCollectableCards(hand.get(i), game);
            int totalPoints = ScoreCalculation.calculateTotalPoints(collectableCards);

            if (totalPoints < lowestPoints) {
                lowestPoints = totalPoints;
                bestIdx = i;
            }
        }
        return bestIdx;
    }
}
