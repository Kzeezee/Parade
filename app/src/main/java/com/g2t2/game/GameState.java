package com.g2t2.game;

import java.io.Serializable;
import java.util.ArrayList;

import com.g2t2.types.*;

/*
 * This class is used to track the status each instance of a game
 * ie. num of player, GameId, who is the gameMaster, the paradeboard, and deck
 */
public class GameState implements Serializable { // to allow it to be sent over
    private static final long serialVersionUID = 1L; // Define serialVersionUID
    private int numOfPlayers;
    private String gameId;
    private Player gameMaster;

    private ArrayList<Player> players = new ArrayList<>();
    private ArrayList<Card> parade = new ArrayList<>();
    private CardDeck deck = new CardDeck();

    public GameState(String gameId) {
        this.gameId = gameId;
    }

    public void addPlayer(Player player) {
        numOfPlayers++;
        players.add(player);

        if (numOfPlayers == 1) { // first person becomes gameMaster
            gameMaster = players.get(0);
        }
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public String getGameId() {
        return gameId;
    }

    public Player getGameMaster() {
        return gameMaster;
    }

    public void setGameMaster(Player gameMaster) {
        this.gameMaster = gameMaster;
    }

    public CardDeck getDeck() {
        return deck;
    }

    public ArrayList<Card> getParade() {
        return parade;
    }

    public void setParade(ArrayList<Card> parade) {
        this.parade = parade;
    }
}
