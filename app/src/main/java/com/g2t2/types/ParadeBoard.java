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

    public static CardDeck getDECK() {
        return DECK;
    }

    public static List<Card> getParade() {
        return parade;
    }

    public static void setParade(List<Card> parade) {
        ParadeBoard.parade = parade;
    }

    public static List<Player> getPlayers() {
        return players;
    }

    public static void setPlayers(List<Player> players) {
        ParadeBoard.players = players;
    }

}