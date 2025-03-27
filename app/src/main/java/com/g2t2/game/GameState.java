package com.g2t2.game;

import java.io.Serializable;
import java.util.*;

import com.g2t2.types.*;

/*
 * This class is used to track the status each instance of a game
 * ie. num of player, GameId, who is the gameMaster, the paradeboard, and deck
 */
public class GameState implements Serializable { // to allow it to be sent over
    private int numOfPlayers;
    private String gameId;
    private Player gameMaster;

    private List<Player> players = new ArrayList<>();
    private List<Card> parade = new ArrayList<>();
    private CardDeck deck = new CardDeck();
    private boolean hasStarted = false;

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

    // public int getNumOfPlayers() {
    //     return numOfPlayers;
    // }

    public List<Player> getPlayers() {
        return players;
    }

    // public void setPlayers(List<Player> players) {
    //     this.players = players;
    // }

    public String getGameId() {
        return gameId;
    }

    // public Player getGameMaster() {
    //     return gameMaster;
    // }

    // public void setGameMaster(Player gameMaster) {
    //     this.gameMaster = gameMaster;
    // }

    public boolean hasStarted() {
        return hasStarted;
    }

    public void setHasStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
    }

    public CardDeck getDeck() {
        return deck;
    }

    public List<Card> getParade() {
        return parade;
    }

    public void setParade(List<Card> parade) {
        this.parade = parade;
    }
}
