package com.g2t2.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.g2t2.types.Player;
import com.g2t2.util.Constants;
import com.g2t2.util.Display;
import com.g2t2.util.Utility;

/**
 * Represents an online lobby for players to connect and interact before starting a game.
 *
 * This class manages the list of connected players and provides methods to display
 * the lobby and update player information. It serves as the entry point for players
 * in an online multiplayer environment.
 * 
 * @author Ben
 */
public class OnlineLobbyTemp {
    // public static List<Player> connectedPlayers = new ArrayList<>(); // A list of players currently connected to the online lobby.


    public static void displayLobby() {
        Utility.clearConsoleScreen();
        System.out.println(Constants.ONLINE_LOBBY_BANNER);
        System.out.println(Constants.DIVIDER);
    }
}
