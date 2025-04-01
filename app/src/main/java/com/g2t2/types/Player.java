package com.g2t2.types;

import java.io.Serializable;
import java.util.*;

/**
 * Player class, modelling a player in Parade.
 * 
 * @author Ben
 */

public class Player implements Serializable {
    private static int counter = 1;  // autoincrement
    private int playerId;
    private String name;
    private List<Card> cardsOnHand = new ArrayList<>();
    private List<Card> cardsInCollection = new ArrayList<>();;
    private int score;
    private boolean isCPU = false;
    private int difficulty;
    private PlayerProfile playerProfile;
    private int collectionSize;

    /**
     * Creates a new Player with the specified name.
     * 
     * @param name the name of the player
     */
    public Player(String name) {
        playerId = counter++;
        this.name = name;
        cardsInCollection = new ArrayList<>();
    }

    /**
     * Creates a new Player, specifying if they are a CPU and their CPU difficulty
     * level.
     * 
     * @param isCPU      boolean value describing if the player is a CPU
     * @param difficulty difficulty level of the CPU
     *                   1 = easy, 2 = medium, 3 = hard
     */
    public Player(boolean isCPU, int difficulty) {
        playerId = counter++;
        name = "CPU" + playerId;
        this.isCPU = isCPU;
        this.difficulty = difficulty;
        cardsInCollection = new ArrayList<>();
    }

    /**
     * Creates a starting hand of 5 random cards for the Player.
     * 
     * @param deck the deck of cards to be drawn from
     */
    public void drawStartingCards(CardDeck deck) {
        // draw 5 cards from the deck
        for (int i = 0; i < 5; i++) {
            cardsOnHand.add(deck.drawCard());
        }
    }

    /**
     * Adds a random card from the deck to the Player's hand.
     * 
     * @param deck the deck of cards to be drawn from
     */
    public void drawCard(CardDeck deck) {
        cardsOnHand.add(deck.drawCard());
    }

    /**
     * Returns the player's ID as an integer.
     * 
     * @return the player's ID
     */
    public int getPlayerId() {
        return playerId;
    }

    /**
     * Returns the player's name as a String.
     * 
     * @return the player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the player's hand as a List of Card.
     * 
     * @return the player's cards in their hand
     */
    public List<Card> getCardsOnHand() {
        return cardsOnHand;
    }

    /**
     * Returns the player's collection as a List of Card.
     * 
     * @return the player's cards in their collection
     */
    public List<Card> getCardsInCollection() {
        return cardsInCollection;
    }

    /**
     * Sets the player's collection to the specified collection.
     * 
     * @param cardsInCollection the collection to be set to
     */
    public void setCardsInCollection(List<Card> cardsInCollection) {
        this.cardsInCollection = cardsInCollection;
    }

    /**
     * Returns the player's score as an integer.
     * 
     * @return the player's score
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the score to the specified score.
     * 
     * @param score the score to be set to
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Returns whether or not the player is a CPU as a boolean.
     * 
     * @return {@code true} if the player is CPu, else {@code false}
     */
    public boolean getCPU() {
        return isCPU;
    }

    public PlayerProfile getPlayerProfile() {
        return playerProfile;
    }

    public void setPlayerProfile(PlayerProfile playerProfile) {
        this.playerProfile = playerProfile;
    }

    /**
     * Returns the size of player's cardsInCollection
     * 
     * @return the collection size
     */
    public int getCollectionSize() {
        return collectionSize;
    }

    /**
     * Update the collection size based on player's cardsInCollection
     * 
     */
    public void updateCollectionSize() {
        collectionSize = cardsInCollection.size();
    }

    /**
     * Returns the difficulty level of the CPU as an integer.
     * 
     * @return the difficulty level of the CPU
     */
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * Checks whether a player is equal to another based on their name and playerID.
     * 
     * @param o the object to be compared to the player
     * @return true if the name and playerID are the same; false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof Player player) {
            if (player.name.equals(this.name) && player.playerId == this.playerId) {
                return true;
            }
        }
        return false;
    }
}