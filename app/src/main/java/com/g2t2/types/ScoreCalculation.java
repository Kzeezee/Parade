package com.g2t2.types;

import java.util.*;

import com.g2t2.enums.*;
import com.g2t2.util.*;

/**
 * Score Calculation class to check for end game condition after every move
 * played
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
        ArrayList<Card> cards = player.getCardsInCollection();

        // RED, BLUE, PURPLE, GREEN, GREY, ORANGE
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
    public static boolean hasSixColours(Map<CardColour, Integer> cardColourMap) {
        Set<CardColour> keys = cardColourMap.keySet();
        if (keys.size() != 6) {
            return false;
        }
        for (CardColour key : keys) {
            if (cardColourMap.get(key) == 0) {
                return false;
            }
        }
        return true;
    }

    // /**
    //  * Computer the score when the game ends
    //  *
    //  * @param players The list of players in the current game
    //  * @return a HashMap of Player as key and Score (Integer) as value
    //  */
    // public static Map<Player, Integer> computeScore(List<Player> players) {
    //     Map<Player, Integer> scoreMap = new HashMap<>();

    //     List<CardColour> cardColourList = Constants.cardColours;

    //     for (CardColour colour : cardColourList) {
    //         discardMajorityPiles(players, scoreMap, colour);
    //     }

    //     for (Player player : players) {
    //         int score = 0;
    //         List<Card> cardList = player.getCardsInCollection();
    //         for (Card card : cardList) {
    //             System.out.println(card);
    //             score += card.getValue();
    //         }
    //         if (scoreMap.containsKey(player)) {
    //             scoreMap.put(player, scoreMap.get(player) + score);
    //         } else {
    //             scoreMap.put(player, score);
    //         }

    //         for (Player p : players) {
    //             System.out.println(p.getName() + " has " + scoreMap.get(p));
    //         }
    //     }

    //     return scoreMap;
    // }

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

        for (Player player : players) {
            int score = 0;
            List<Card> cardList = player.getCardsInCollection();
            for (Card card : cardList) {
                System.out.println(card);
                score += card.getValue();
            }
            if (scoreMap.containsKey(player)) {
                scoreMap.put(player, scoreMap.get(player) + score);
            } else {
                scoreMap.put(player, score);
            }
        }

        for (Player player: players) {
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
    public static void discardMajorityPiles(List<Player> players, Map<Player, Integer> scoreMap, CardColour cardColour) {
        List<Integer> colourCountList = new ArrayList<>();
        for (Player player : players) {
            int count = countCardColour(player.getCardsInCollection(), cardColour);
            colourCountList.add(count);
        }
        int max = Collections.max(colourCountList);
        if (max == 0) {
            return;
        }
        if (players.size() == 2) {
            int diff = colourCountList.get(0) - colourCountList.get(1);
            if (Math.abs(diff) < 2) {
                return;
            }
        }
        // remove the cards from the player(s) if they have max card number for the colour
        int idx = colourCountList.indexOf(max);
        while (idx >= 0) {
            Player player = players.get(idx);
            if (scoreMap.containsKey(player)) {
                scoreMap.put(player, scoreMap.get(player) + 1);
            } else {
                scoreMap.put(player, 1);
            }
            colourCountList.set(idx, 0);
            List<Card> cardsList = player.getCardsInCollection();
            cardsList.removeIf(card -> card.getColour() == cardColour);
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
    public static int countCardColour(List<Card> cards, CardColour cardColour) {
        int count = 0;
        for (Card card : cards) {
            if (card.getColour() == cardColour) {
                count++;
            }
        }
        return count;
    }

    // Testing compute Score
    // public static void main(String[] args) {
    //     CardDeck testDeck = new CardDeck();
    //     Player p1 = new Player("p1", testDeck);
    //     Player p2 = new Player("p2", testDeck);

    //     ArrayList<Card> p1Cards = new ArrayList<>();
    //     for (int i = 0; i < 5; i++) {
    //         p1Cards.add(new Card(i, CardColour.RED));
    //     }
    //     for (int i = 0; i < 5; i++) {
    //         p1Cards.add(new Card(i, CardColour.GREEN));
    //     }
    //     for (int i = 0; i < 2; i++) {
    //         p1Cards.add(new Card(i, CardColour.ORANGE));
    //     }
    //     for (int i = 1; i < 3; i++) {
    //         p1Cards.add(new Card(i, CardColour.PURPLE));
    //     }

    //     ArrayList<Card> p2Cards = new ArrayList<>();
    //     for (int i = 0; i < 2; i++) {
    //         p2Cards.add(new Card(i + 5, CardColour.RED));
    //     }
    //     for (int i = 1; i < 3; i++) {
    //         p2Cards.add(new Card(i + 5, CardColour.GREEN));
    //     }
    //     for (int i = 0; i < 5; i++) {
    //         p2Cards.add(new Card(i + 5, CardColour.ORANGE));
    //     }
    //     for (int i = 0; i < 5; i++) {
    //         p2Cards.add(new Card(i + 5, CardColour.PURPLE));
    //     }

    //     p1.setCardsInCollection(p1Cards);
    //     p2.setCardsInCollection(p2Cards);

    //     ArrayList<Player> players = new ArrayList<>();
    //     players.add(p1);
    //     players.add(p2);
    //     ScoreCalculation scoreCal = new ScoreCalculation();
    //     Map<Player, Integer> scoreMap = scoreCal.computeScore(players);

    //     for (Player p : players) {
    //         System.out.println(p.getName() + " has " + scoreMap.get(p) + "points");
    //     }

    // }
}

// javac -d output -cp "main/java" main/java/com/g2t2/types/ScoreCalculation.java
// java -cp "output:main/java" com.g2t2.types.ScoreCalculation
// /Users/hswt/Desktop/SMU Documents/Y1T2/CS102 Programming Fundamentals II/ParadeFolder/Parade/app/src/main/java/com/g2t2/types/test.java