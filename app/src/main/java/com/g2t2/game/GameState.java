package com.g2t2.game;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;

import com.g2t2.types.*;

/*
 * This class is used to track the status each instance of a game
 * ie. num of player, GameId, who is the gameMaster, the paradeboard, and deck
 */
public class GameState implements Serializable { // to allow it to be sent over
    private static final long serialVersionUID = 1L;
    private int numOfPlayers;
    private String gameId;
    private Player gameMaster;

    private List<Player> players = new ArrayList<>();
    private List<Card> parade = new ArrayList<>();
    private CardDeck deck = new CardDeck();
    private boolean hasStarted = false;

    public GameState() {
        gameId = generateGameId();
    }

    private String generateGameId() {
        long timestamp = Instant.now().getEpochSecond();
        Random random = new Random();
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder randomPart = new StringBuilder();
        for (int i = 0; i < 4; i++) { // Generate 4 random characters
            randomPart.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return "G" + timestamp + "-" + randomPart; // Format: G<timestamp>-<random>
    }

    public void addPlayer(Player player) {
        numOfPlayers++;
        players.add(player);

        if (numOfPlayers == 1) { // first person becomes gameMaster
            gameMaster = players.get(0);
        }
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void printPlayerList() {
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            // getting the role of the player
            String playerRole = "Player";
            playerRole = player.getCPU() ? "CPU" : "Player";
            playerRole = gameMaster.equals(player) ? "GameMaster" : "Player";
            System.out.println(i+1 + ") " + player.getName() + " (id=" + player.getPlayerId() 
            + ") (" + playerRole + ")");
        }
    }

    public String getGameId() {
        return gameId;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    public Player getGameMaster() {
        return gameMaster;
    }

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

    // @Override
    // public boolean equals(Object o) {
    //     if (o instanceof GameState gameState) {
    //         return this.getGameId().equals(gameState.getGameId());
    //     }
    //     return false;
    // }

    // @Override
    // public int hashCode() {
    //     return Objects.hash(gameId);
    // }

    // Implement deepCopy() in GameState
    public GameState deepCopy() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            oos.flush();
            
            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            return (GameState) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Deep copy failed", e);
        }
    }
}
