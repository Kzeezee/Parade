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

/**
 * Represents the state of a game instance.
 * 
 * This class tracks all essential game-related information, such as the number
 * of players, game ID, the game master, the current player, the parade board,
 * and the card deck. It also manages game initialization, turn progression,
 * and serialization for saving or transmitting game states.
 * 
 * @author Ben
 */
public class GameState implements Serializable { // to allow it to be sent over
    private static final long serialVersionUID = 1L;
    private int numOfPlayers; // The total number of players in the game.
    private String gameId;
    private Player gameMaster;
    private Player currentPlayer; // turn of the current Player
    private Player playerWhoStartFinalRound;
    private List<Player> players = new ArrayList<>();
    private List<Card> paradeBoard = new ArrayList<>();
    private CardDeck deck = new CardDeck();
    private boolean hasStarted = false; // Flag indicating whether the game has started.
    private boolean islastRound = false;
    private boolean isPut2CollectionRound = false;
    private boolean isEndOfGame = false;

    /**
     * Constructs a new GameState instance and generates a unique game ID.
     */
    public GameState() {
        gameId = generateGameId();
    }

    /**
     * Generates a unique ID for this game instance based on the current timestamp
     * and random characters.
     *
     * @return a string representing the unique game ID
     */
    private String generateGameId() {
        long timestamp = Instant.now().getEpochSecond();
        Random random = new Random();
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder randomPart = new StringBuilder();
        for (int i = 0; i < 2; i++) { // Generate 2 random characters
            randomPart.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return "G" + timestamp + "-" + randomPart; // Format: G<timestamp>-<random>
    }

    /**
     * Gets the unique identifier for this game session.
     *
     * @return the game ID string that uniquely identifies this game
     */
    public String getGameId() {
        return gameId;
    }

    /**
     * Adds a player to the game. The first player added becomes the game master.
     *
     * @param player the player to add to the game
     */
    public void addPlayer(Player player) {
        numOfPlayers++;
        players.add(player);
        if (numOfPlayers == 1) { // first person becomes gameMaster
            gameMaster = players.get(0);
        }
    }
    /**
     * Removes a player from the game and decrements the total player count.
     * 
     * @param player the player to remove from the game
     */

    public void removePlayer(Player player) {
        players.remove(player);
        numOfPlayers--;
    }

    /**
     * Retrieves a list of all players in the game.
     *
     * @return a list of players participating in the game
     */
    public List<Player> getPlayers() {
        return players;
    }

        /**
     * Gets the total number of players in the game.
     *
     * @return the total number of players in the game
     */
    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    /**
     * Gets the number of CPU players in the game.
     *
     * @return the number of CPU players in the game
     */
    public int getNumOfCPU() {
        int count = 0;
        for (Player player : players) {
            if (player.getCPU()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Checks if a player is a CPU player
     *
     * @param playerId the ID of the player to check
     * @return {@code true} if the player is a CPU player, {@code false} otherwise
     */
    public boolean checkPlayerIsCPU(int playerId) {
        for (Player player : players) {
            if (player.getPlayerId() == playerId) {
                if (player.getCPU()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Finds and returns a player based on their player ID.
     *
     * @param yourPlayerId the ID of the player to find
     * @return the Player object with the matching ID, or null if no match is found
     */
    public Player findMyPlayer(int yourPlayerId) {
        for (Player player : players) {
            if (player.getPlayerId() == yourPlayerId) {
                return player;
            }
        }
        return null;
    }

    /**
     * Prints a list of all players, including their roles (e.g., GameMaster or CPU).
     */
    public void printPlayerList() {
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            // printing depending on role
            if (player.getCPU()) {
                System.out.println(" " + (i + 1) + ") " + player.getName() + " (id=" + player.getPlayerId()
                        + ") (CPU, difficulty = " + player.getDifficulty() + ")");
            } else {
                String playerRole = gameMaster.equals(player) ? "GameMaster" : "Player";
                System.out.println(" " + (i + 1) + ") " + player.getName() + " (id=" + player.getPlayerId()
                        + ") (" + playerRole + ")");
            }
        }
    }

    /**
     * Gets the deck of cards in the game.
     *
     * @return the deck of cards in the game
     */
    public CardDeck getDeck() {
        return deck;
    }

    /**
     * Gets the parade board in the game.
     *
     * @return the parade board in the game
     */
    public List<Card> getParade() {
        return paradeBoard;
    }

    /**
     * Sets the parade board in the game.
     *
     * @param paradeBoard the parade board in the game
     */
    public void setParade(List<Card> paradeBoard) {
        this.paradeBoard = paradeBoard;
    }

    /**
     * Distributes initial cards to all players and sets up the parade board.
     *
     * This method is responsible for initialising the game by:
     * - Giving each player their starting hand of cards from the deck.
     * - Setting up the parade board with six cards drawn from the deck.
     * It ensures that players and the parade board are ready for gameplay.
     */
    public void giveOutInitialCards() {
        for (Player player : players) {
            player.drawStartingCards(deck);
        }
        // set parade board
        for (int i = 0; i < 6; i++) {
            paradeBoard.add(deck.drawCard());
        }
    }

    /**
     * Starts the game and shuffle the list of players and first player of the list starts.
     */
    public void startGame() {
        hasStarted = true;
        Collections.shuffle(players);
        currentPlayer = players.get(0);
    }

    /**
     * Passes the turn to the next player in the game.
     */
    public void passTurnToNextPlayer() {
        int indexOfNextPlayer = (players.indexOf(currentPlayer) + 1) % numOfPlayers;
        currentPlayer = players.get(indexOfNextPlayer);
    }

    /**
     * Prints the number of cards left in the deck.
     */
    public void printNumOfCardsLeft() {
        System.out.println(" 📌 Number of Cards Left in the Deck: " + deck.getCardsInDeck().size());
    }

    /**
     * Prints the current player's turn.
     */
    public void printPlayerTurn() {
        System.out.println(" 📌 Currently, it is player " + Display.colorString(currentPlayer.getName()
                + "(" + currentPlayer.getPlayerId() + ")" + Constants.ANSI_RESET + " turn."));
    }

    /**
     * Gets the current player in the game.
     *
     * @return the current player in the game
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Gets the game master of the game.
     *
     * @return the game master of the game
     */
    public Player getGameMaster() {
        return gameMaster;
    }

    /**
     * Sets a new game master for the game.
     *
     * @return {@code true} if a new game master is successfully set, {@code false} otherwise
     */
    public boolean setNewGameMaster() {
        for (Player player : players) {
            if (!player.getCPU()) {
                gameMaster = player;
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the player who starts the final round of the game.
     *
     * @return the player who starts the final round of the game
     */
    public Player getPlayerWhoStartFinalRound() {
        return playerWhoStartFinalRound;
    }

    /**
     * Sets the player who starts the final round of the game.
     *
     * @param playerWhoStartFinalRound the player who starts the final round of the game
     */
    public void setPlayerWhoStartFinalRound(Player playerWhoStartFinalRound) {
        this.playerWhoStartFinalRound = playerWhoStartFinalRound;
    }

    /**
     * Gets a boolean value indicating if the game has started.
     *
     * @return {@code true} if the game has started, {@code false} otherwise
     */
    public boolean getHasStarted() {
        return hasStarted;
    }

    /**
     * Sets the boolean value indicating if the game has started.
     *
     * @param hasStarted the boolean value indicating if the game has started
     */
    public void setHasStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
    }

    /**
     * Sets the boolean value indicating if the game is in the last round.
     *
     * @param isLastRound the boolean value indicating if the game is in the last round
     */
    public void setLastRound(boolean isLastRound) {
        this.islastRound = isLastRound;
    }

    /**
     * Gets the boolean value indicating if the game is in the last round.
     *
     * @return the boolean value indicating if the game is in the last round
     */
    public boolean isLastRound() {
        return islastRound;
    }

    /**
     * Gets the boolean value indicating if the game is in the put 2 collection round.
     *
     * @return the boolean value indicating if the game is in the put 2 collection round
     */
    public boolean isPut2CollectionRound() {
        return isPut2CollectionRound;
    }

    /**
     * Sets the boolean value indicating if the game is in the put 2 collection round.
     *
     * @param isPut2CollectionRound the boolean value indicating if the game is in the put 2 collection round
     */
    public void setPut2CollectionRound(boolean isPut2CollectionRound) {
        this.isPut2CollectionRound = isPut2CollectionRound;
    }

    /**
     * Gets the boolean value indicating if the game is in the end of game.
     *
     * @return the boolean value indicating if the game is in the end of game
     */
    public boolean isEndOfGame() {
        return isEndOfGame;
    }

    /**
     * Sets the boolean value indicating if the game is in the end of game.
     *
     * @param isEndOfGame the boolean value indicating if the game is in the end of game
     */
    public void setEndOfGame(boolean isEndOfGame) {
        this.isEndOfGame = isEndOfGame;
    }

    /**
     * Resets the game state to its initial conditions.
     * 
     * This method clears all game progress and returns the game to its starting state by:
     * - Resetting all game phase flags (hasStarted, isLastRound, isPut2CollectionRound, isEndOfGame)
     * - Clearing the player count and player list
     * - Clearing the parade board
     * - Creating a fresh card deck
     */
    public void resetGame() {
        hasStarted = false; 
        islastRound = false;
        isPut2CollectionRound = false;
        isEndOfGame = false;
        numOfPlayers = 0;
        players = new ArrayList<>();
        paradeBoard = new ArrayList<>();
        deck = new CardDeck();
    }

    /**
     * Creates a complete deep copy of the current game state.
     * 
     * This method creates an entirely independent copy of the GameState object
     * using serialization and deserialization, ensuring that all nested objects
     * are also deep-copied. 
     *
     * @return a new GameState object that is a deep copy of this instance
     * @throws RuntimeException if the serialization or deserialization process fails
     */
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
