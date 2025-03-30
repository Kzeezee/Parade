package com.g2t2.types;

import java.util.*;

/**
 * ParadeBoard class, modelling how the board layout will be like.
 * 
 * @author Ben
 */
public class ParadeBoard {

    private static List<Player> players = new ArrayList<>();
    private static List<Card> parade = new ArrayList<>();
    private static final CardDeck DECK = new CardDeck();


    /**
     * Returns the card deck as a CardDeck
     * 
     * @return the card deck
     */    
    public static CardDeck getDECK() {
        return DECK;
    }

    /**
     * Returns the parade as a List of Card
     * 
     * @return the parade
     */    
    public static List<Card> getParade() {
        return parade;
    }

    /**
     * Sets this parade to the specified parade
     * 
     * @param parade the parade to be set to
     */  
    public static void setParade(List<Card> parade) {
        ParadeBoard.parade = parade;
    }

    /**
     * Returns the players as a List of Player
     * 
     * @return the players
     */  
    public static List<Player> getPlayers() {
        return players;
    }

    /**
     * Sets the List of Player to the specified List of Player
     * 
     * @param players the List of Player to be set to
     */  
    public static void setPlayers(List<Player> players) {
        ParadeBoard.players = players;
    }
}