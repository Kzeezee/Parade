package com.g2t2.game;

import com.g2t2.types.PlayerProfile;

public class StateManager {
    private static PlayerProfile currentPlayerProfile = null;

    public static void setPlayerProfile(PlayerProfile pp) {
        // After setting player profile, we know we which player to track games with
        currentPlayerProfile = pp;
        // Show start menu
        StartMenu.showStartMenu();
    }

    public static PlayerProfile getPlayerProfile() {
        return currentPlayerProfile;
    }
}
