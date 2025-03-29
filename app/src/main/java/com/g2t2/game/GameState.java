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
    private List<Player> players = new ArrayList<>();
    private List<Card> paradeBoard = new ArrayList<>();
    private CardDeck deck = new CardDeck();
    private boolean hasStarted = false;
    private boolean islastRound = false;

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

    public Player getGameMaster() {
        return gameMaster;
    }

    // public void setGameMaster(Player gameMaster) {
    //     this.gameMaster = gameMaster;
    // }

    public boolean hasStarted() {
        return hasStarted;
    }

    public void setIsLastRound(boolean isLastRound) {
        this.islastRound = isLastRound;
    }

    public boolean isLastRound() {
        return islastRound;
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

        for (int i = 0; i < 6; i++) {
            paradeBoard.add(deck.drawCard());
        }
    }

    public void startGame() {
        hasStarted = true;
        // finding a random person to start the game
        int currentPlayerIndex = (int) (Math.random() * players.size());
        currentPlayer = players.get(currentPlayerIndex);


        // int previousPlayer = currentPlayer;
        // // checks the previous player because they are the one that might have a
        // // complete collection (ALL COLOURS)
        // while (!ScoreCalculation.meetsGameEndCondition(players.get(previousPlayer), deck)) {
        //     if (!players.get(currentPlayer).getCPU()) {
        //         playTurn(ParadeBoard.getPlayers().get(currentPlayer), false);
        //     } else {
        //         CPUturn(ParadeBoard.getPlayers().get(currentPlayer), false);
        //     } 
        //     previousPlayer = currentPlayer;
        //     currentPlayer = (currentPlayer + 1) % ParadeBoard.getPlayers().size();
        // }
        // // last round of play
        // for (int i = 0; i < ParadeBoard.getPlayers().size(); i++) {
        //     if (!ParadeBoard.getPlayers().get(currentPlayer).getCPU()) {
        //         playTurn(ParadeBoard.getPlayers().get(currentPlayer), true);
        //     } else {
        //         CPUturn(ParadeBoard.getPlayers().get(currentPlayer), true);
        //     } 
        //     currentPlayer = (currentPlayer + 1) % ParadeBoard.getPlayers().size();
        // }

        // // choose 2 cards in hand to add to collection
        // for (int i = 0; i < ParadeBoard.getPlayers().size(); i++) {
        //     if (!ParadeBoard.getPlayers().get(currentPlayer).getCPU()) {
        //         putTwoToCollections(ParadeBoard.getPlayers().get(currentPlayer));
        //     } else {
        //         putTwoToCPUCollections(ParadeBoard.getPlayers().get(currentPlayer));
        //     } 
        //     currentPlayer = (currentPlayer + 1) % ParadeBoard.getPlayers().size();
        // }
        // ScoreCalculation.computeScore(ParadeBoard.getPlayers());
        // displayScores(ParadeBoard.getPlayers());

        // promptForNewGame();
    }

    public static void printNumOfCardsLeft() {
        System.out.println(" 📌 Number of Cards Left in the Deck: " + game.getDeck().getCardsInDeck().size());
    }

    public static void printPlayerTurn() {
        System.out.println(" 📌 Currently, it is player " + Display.colorString(currentPlayer.getName() 
        + "(" + currentPlayer.getPlayerId() + ")" + Constants.ANSI_RESET + " turn."));
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
