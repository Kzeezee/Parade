package com.g2t2.types;

import java.util.ArrayList;

/**
 * ParadeBoard class, modelling how the board layout will be like.
 * @author Ben
 */
public class ParadeBoard {

    private static ArrayList<Player> players = new ArrayList<>();
    private static ArrayList<Card> parade = new ArrayList<>();
    private static final CardDeck DECK = new CardDeck(); // everyone use this single card deck?

    public static CardDeck getDECK() {
        return DECK;
    }

    public static ArrayList<Card> getParade() {
        return parade;
    }

    public static void setParade(ArrayList<Card> parade) {
        ParadeBoard.parade = parade;
    }

    public static ArrayList<Player> getPlayers() {
        return players;
    }

    public static void setPlayers(ArrayList<Player> players) {
        ParadeBoard.players = players;
    }

}