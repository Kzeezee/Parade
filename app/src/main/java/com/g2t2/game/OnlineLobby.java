package com.g2t2.game;

import java.util.*;

import com.g2t2.types.Player;
import com.g2t2.util.Constants;
import com.g2t2.util.Utility;

public class OnlineLobby {

    public static List<Player> connectedPlayers = new ArrayList<>();

    public static void displayLobby() {
        Utility.clearConsoleScreen();
        System.out.println(Constants.ONLINE_LOBBY_BANNER);
        System.out.println(Constants.DIVIDER);

        
    }

    public static void updatePlayers() {

    }
}
