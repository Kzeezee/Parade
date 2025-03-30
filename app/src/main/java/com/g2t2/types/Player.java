package com.g2t2.types;

import java.io.Serializable;
import java.util.*;

/**
 * Player class, modelling a player in Parade.
 * 
 * @author Ben
 */

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int counter = 1;  // autoincrement
    private int playerId;
    private String name;
    private List<Card> cardsOnHand = new ArrayList<>();
    private List<Card> cardsInCollection = new ArrayList<>();;
    private int score;
    private boolean isCPU = false;
    private int difficulty;

    public Player(String name) {
        playerId = counter++;
        this.name = name;
    }

    public Player(boolean isCPU, int difficulty) {
        playerId = counter++;
        name = "CPU" + playerId;
        this.isCPU = isCPU;
        this.difficulty = difficulty;
    }

    public void drawStartingCards(CardDeck deck) {
        // draw 5 cards from the deck
        for (int i = 0; i < 5; i++) {
            cardsOnHand.add(deck.drawCard());
        }
    }

    public void drawCard(CardDeck deck) {
        cardsOnHand.add(deck.drawCard());
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getName() {
        return name;
    }

    public List<Card> getCardsOnHand() {
        return cardsOnHand;
    }

    public List<Card> getCardsInCollection() {
        return cardsInCollection;
    }

    public void setCardsInCollection(List<Card> cardsInCollection) {
        this.cardsInCollection = cardsInCollection;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean getCPU(){
        return isCPU;
    }
    
    public int getDifficulty() {
        return difficulty;
    }

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