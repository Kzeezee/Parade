package com.g2t2.network.networkUtility;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

import com.g2t2.game.GameState;
import com.g2t2.types.Player;

/**
* The NetworkUtility class provides static utility methods to assist in 
* sending game-related objects across a network using {@link ObjectOutputStream}.
*
* It also provides helper methods to locate game instances and handle thread delays safely.
* 
* All methods are static
* 
* @author Ben
*/
public class NetworkUtility {

    /**
     * Searches for a game with the specified gameId in the provided map of available games.
     *
     * @param gameId the unique identifier for the game
     * @param availableGames the list available games stored in a Map between gameID and {@link GameState} objects
     * @return the {@link GameState} object if found, otherwise null
     */
    public static GameState findGameState(String gameId, Map<String, GameState> availableGames) {
        GameState game = availableGames.get(gameId);
        if (game != null) {
            return game;
        }
        return null;
    }

    /**
     * Sends a deep copy (snapshot) of the {@link GameState} object 
     * over the network to ensure that the original is not modified.
     *
     * @param game the current game state to send
     * @param objectOutputStream the output stream to send the object through
     * @throws IOException if an I/O error occurs during writing
     */
    public static void sendGameState(GameState game, ObjectOutputStream objectOutputStream) throws IOException {
        GameState snapshot = game.deepCopy(); // create a snapshot
        objectOutputStream.writeObject(snapshot);
        objectOutputStream.flush();
    }

    /**
     * Sends a {@link Player} object through the specified output stream.
     *
     * @param player the player object to send
     * @param objectOutputStream the output stream to send the player through
     * @throws IOException if an I/O error occurs during writing
     */
    public static void sendPlayer(Player player, ObjectOutputStream objectOutputStream) throws IOException  {
        objectOutputStream.writeObject(player);
        objectOutputStream.flush();
    }

    /**
     * Wraps a String array message into a {@link Message} object and sends it over the specified output stream.
     *
     * @param message the array of strings representing the message content
     * @param objectOutputStream the output stream to send the message through
     * @throws IOException if an I/O error occurs during writing
     */
    public static void sendMessage(String message[], ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeObject(new Message(message));
        objectOutputStream.flush();
    }

    /**
     * Causes the current thread to sleep for the specified duration.
     * If interrupted, throws an {@link IOException}.
     *
     * @param duration the time to sleep in milliseconds
     * @throws IOException if the sleep is interrupted
     */
    public static void threadSleep(int duration) throws IOException {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            throw new IOException();
        }
    }
}
