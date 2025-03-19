package com.g2t2.types;

import java.util.*;

/**
 * Player class, modelling a player in Parade.
 * 
 * @author Ben
 */

public class Player {

    private static int numOfPlayers; // automatically increases whenever a player is created

    private int playerId; // autoassigned based on the number of players
    private String name;
    private ArrayList<Card> cardsOnHand;
    private ArrayList<Card> cardsInCollection;
    private int score;

    public Player(String name, CardDeck deck) {
        numOfPlayers++;
        playerId = numOfPlayers;
        this.name = name;

        // draw 5 cards from the deck
        cardsOnHand = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            cardsOnHand.add(deck.drawCard());
        }

        cardsInCollection = new ArrayList<>();
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

    public ArrayList<Card> getCardsOnHand() {
        return cardsOnHand;
    }

    public ArrayList<Card> getCardsInCollection() {
        return cardsInCollection;
    }

    public void setCardsInCollection(ArrayList<Card> cardsInCollection) {
        this.cardsInCollection = cardsInCollection;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

}