package com.g2t2.network.networkUtility;

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

    public static void threadSleep(int duration) throws InterruptedException {
        Thread.sleep(duration);
    }
}
