package com.g2t2.network.networkUtility;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;

import com.g2t2.game.GameState;
import com.g2t2.types.Player;

public class NetworkUtility {

    public static GameState findGameState(String gameId, Map<String, GameState> availableGames) {
        GameState game = availableGames.get(gameId);
        if (game != null) {
            return game;
        }
        return null;
    }

    public static Player findMyPlayer(GameState game, int yourPlayerId) {
        for (Player player : game.getPlayers()) {
            if (player.getPlayerId() == yourPlayerId) {
                return player;
            }
        }
        return null;
    }

    public static void sendGameState(GameState game, ObjectOutputStream objectOutputStream) throws IOException {
        GameState snapshot = game.deepCopy(); // Create a snapshot
        objectOutputStream.writeObject(snapshot);
        objectOutputStream.flush();
    }

    public static void sendPlayer(Player player, ObjectOutputStream objectOutputStream) throws IOException  {
        objectOutputStream.writeObject(player);
        objectOutputStream.flush();
    }

    public static void sendMessage(String message[], ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeObject(new Message(message));
        objectOutputStream.flush();
    }

    public static void threadSleep(int duration) throws InterruptedException {
        Thread.sleep(duration);
    }
}
