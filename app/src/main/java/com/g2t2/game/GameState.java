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
import com.g2t2.util.Constants;
import com.g2t2.util.Display;

/*
 * This class is used to track the status each instance of a game
 * ie. num of player, GameId, who is the gameMaster, the paradeboard, and deck
 */
public class GameState implements Serializable { // to allow it to be sent over
    private static final long serialVersionUID = 1L;
    private int numOfPlayers;
    private String gameId;
    private Player gameMaster;
    private Player currentPlayer; // turn of the current Player
    private Player playerWhoStartFinalRound;
    private List<Player> players = new ArrayList<>();
    private List<Card> paradeBoard = new ArrayList<>();
    private CardDeck deck = new CardDeck();
    private boolean hasStarted = false;
    private boolean islastRound = false;
    private boolean isPut2CollectionRound = false;

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

    public synchronized void addPlayer(Player player) {
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
            // printing depending on role
            if (player.getCPU()) {
                System.out.println(i+1 + ") " + player.getName() + " (id=" + player.getPlayerId() 
                + ") (CPU, difficulty = " + player.getDifficulty() + ")");
            } else {
                String playerRole = gameMaster.equals(player) ? "GameMaster" : "Player";
                System.out.println(i+1 + ") " + player.getName() + " (id=" + player.getPlayerId() 
                + ") (" + playerRole + ")");
            }
        }
    }

    public String getGameId() {
        return gameId;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    
    public Player getGameMaster() {
        return gameMaster;
    }

    // public void setGameMaster(Player gameMaster) {
    //     this.gameMaster = gameMaster;
    // }

    public Player getPlayerWhoStartFinalRound() {
        return playerWhoStartFinalRound;
    }

    public void setPlayerWhoStartFinalRound(Player playerWhoStartFinalRound) {
        this.playerWhoStartFinalRound = playerWhoStartFinalRound;
    }

    public boolean hasStarted() {
        return hasStarted;
    }

    public void setLastRound(boolean isLastRound) {
        this.islastRound = isLastRound;
    }

    public boolean isLastRound() {
        return islastRound;
    }

    public boolean isPut2CollectionRound() {
        return isPut2CollectionRound;
    }

    public void setPut2CollectionRound(boolean isPut2CollectionRound) {
        this.isPut2CollectionRound = isPut2CollectionRound;
    }

    public CardDeck getDeck() {
        return deck;
    }

    public List<Card> getParade() {
        return paradeBoard;
    }

    public void setParade(List<Card> parardeBoard) {
        this.paradeBoard = parardeBoard;
    }

    public void giveOutInitialCards() {
        for (Player player : players) {
            player.drawStartingCards(deck);
        }
        // set parade board
        for (int i = 0; i < 6; i++) {
            paradeBoard.add(deck.drawCard());
        }
    }

    public void startGame() {
        hasStarted = true;
        // finding a random person to start the game
        int currentPlayerIndex = (int) (Math.random() * players.size());
        currentPlayer = players.get(currentPlayerIndex);

    }

    public void passTurnToNextPlayer() {
        int indexOfNextPlayer = (players.indexOf(currentPlayer) + 1) % numOfPlayers;
        currentPlayer = players.get(indexOfNextPlayer);
    }

    public void printNumOfCardsLeft() {
        System.out.println(" 📌 Number of Cards Left in the Deck: " + deck.getCardsInDeck().size());
    }

    public void printPlayerTurn() {
        System.out.println(" 📌 Currently, it is player " + Display.colorString(currentPlayer.getName() 
        + "(" + currentPlayer.getPlayerId() + ")" + Constants.ANSI_RESET + " turn."));
    }

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
}
