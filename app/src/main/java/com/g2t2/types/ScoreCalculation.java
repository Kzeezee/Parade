package com.g2t2.types;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

import com.g2t2.enums.CardColour;

import com.g2t2.util.Constants;

/**
 * Score Calculation class to check for end game condition after every
 * moveplayed
 * as well as to compute score for the players after the game ends
 * 
 * @author Shwe
 */
public class ScoreCalculation {

    /**
     * check whether at least one of the end game conditions
     * (no cards in the deck / collected six colours) are met
     *
     * @param player The player who made the last move
     * @param deck   The deck of card being currently played
     * @return {@code true} if a condition is met, else {@code false}
     */
    public static boolean meetsGameEndCondition(Player player, CardDeck deck) {
        if (deck.isEmpty()) {
            return true;
        }
        List<Card> cards = player.getCardsInCollection();

        // RED, BLUE, YELLOW, PURPLE, GREEN, BLACK
        // map the colour card numbers to a hashmap
        Map<CardColour, Integer> cardColourMap = new HashMap<>();
        for (Card card : cards) {
            CardColour cardColour = card.getColour();
            if (cardColourMap.containsKey(cardColour)) {
                cardColourMap.put(cardColour, cardColourMap.get(cardColour) + 1);
            } else {
                cardColourMap.put(cardColour, 1);
            }
        }
        return hasSixColours(cardColourMap);

    }

    /**
     * Check whether the player possess all six colours now based on the parameter
     * cardColourMap
     *
     * @param cardColourMap The hashmap that has CardColour as its key and
     *                      Integer which is the count of each card as value
     * @param deck          The deck of card being currently played
     * @return {@code true} if the hashMap has at least a card each for all six
     *         colours, else {@code false}
     */
    private static boolean hasSixColours(Map<CardColour, Integer> cardColourMap) {
        Set<CardColour> keys = cardColourMap.keySet();
        if (keys.size() < Constants.CARD_COLOURS.size()) {
            return false;
        }

        for (CardColour key : keys) {
            if (cardColourMap.get(key) == 0) {
                return false;
            }
        }
        return true;
    }

    public static int calculateTotalPoints(List<Card> cardsCollected) {
        int sum = 0;
        for (Card card: cardsCollected) {
            sum += card.getValue();
        }
        return sum;
    }

    /**
     * Computer the score when the game ends and set the score of each player
     *
     * @param players The list of players in the current game
     */
    public static void computeScore(List<Player> players) {
        Map<Player, Integer> scoreMap = new HashMap<>();

        List<CardColour> cardColourList = Constants.CARD_COLOURS;

        for (CardColour colour : cardColourList) {
            discardMajorityPiles(players, scoreMap, colour);
        }

        // compute and update the score
        for (Player player : players) {
            int score = 0;
            List<Card> cardList = player.getCardsInCollection();
            for (Card card : cardList) {
                score += card.getValue();
            }

            updateScore(scoreMap, player, score);
        }

        for (Player player : players) { // set the score after computation
            player.setScore(scoreMap.get(player));
        }
    }

    /**
     * discard the cards of player(s) whose pile with highest number of card
     * collected
     *
     * @param players    The list of players in the current game
     * @param scoreMap   The HashMap of Player as key and Score (Integer) as value
     * @param cardColour The colour of the cards we want to deal with
     * 
     */
    private static void discardMajorityPiles(List<Player> players, Map<Player, Integer> scoreMap,
            CardColour cardColour) {
        // count the number of same colour cards for each player and add to an ArrayList
        List<Integer> colourCountList = new ArrayList<>();
        for (Player player : players) {
            int count = countCardColour(player.getCardsInCollection(), cardColour);
            colourCountList.add(count);
        }

        int max = Collections.max(colourCountList);
        if (max == 0) { // no need to discard cards
            return;
        }

        if (players.size() == 2) { // a different implementation for 2 player game
            int diff = colourCountList.get(0) - colourCountList.get(1);
            if (Math.abs(diff) < 2) {
                return;
            }
        }

        // remove the cards from the player(s) & update score if they have max card
        // number for the colour
        int idx = colourCountList.indexOf(max);
        while (idx >= 0) {
            Player player = players.get(idx);
            updateScore(scoreMap, player, colourCountList.get(idx)); // update score based on the number of that
                                                                     // cardColour they have
            player.getCardsInCollection().removeIf(card -> card.getColour() == cardColour);

            // check if there are still anyone else that share the same max score
            colourCountList.set(idx, 0);
            idx = colourCountList.indexOf(max);
        }
    }

    /**
     * Count the number of cards with the specified colour
     *
     * @param cards      the List of the cards
     * @param cardColour The colour of the cards we want to count
     * @return the number of cards
     * 
     */
    private static int countCardColour(List<Card> cards, CardColour cardColour) {
        int count = 0;
        for (Card card : cards) {
            if (card.getColour() == cardColour) {
                count++;
            }
        }
        return count;
    }

    /**
     * Update the score in the scoreMap based on whether the key exists or not
     * if the key exists, simply add the new score to the original value
     * if the key does not exists, create a new entry into the score Map
     *
     * @param ScoreMap The HashMap of Player as key and Score (Integer) as value
     * @param player   The player whose scores we are trying to update
     * @param score    The score to be updated
     * 
     */
    private static void updateScore(Map<Player, Integer> scoreMap, Player player, int score) {
        if (scoreMap.containsKey(player)) {
            scoreMap.put(player, scoreMap.get(player) + score);
        } else {
            scoreMap.put(player, score);
        }
    }
}