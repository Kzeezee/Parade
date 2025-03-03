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


// // testing players in GameInit.java under displayGameOption()
// CardDeck cd = new CardDeck();
// Player p1 = new Player("Abby", cd);
// Player p2 = new Player("Barry", cd);
// Player p3 = new Player("Casey", cd);
// Player p4 = new Player("David", cd);
// ArrayList<Player> players = new ArrayList<>();
// players.add(p1);
// players.add(p2);
// players.add(p3);
// players.add(p4);

// p3.drawCard(cd);

// System.out.println("-- PRINTING PLAYERS --");
// System.out.println();
// for (Player p : players) {
//     System.out.println("Player: " + p.getName() + ", " + p.getPlayerId());
//     for (Card c : p.getCardsOnHand()) {
//         System.out.println("Colour = " + c.getColour() + " || Value = " + c.getValue());
//     }
// }

// System.out.println("Number of cards left " + cd.getDeck().size());