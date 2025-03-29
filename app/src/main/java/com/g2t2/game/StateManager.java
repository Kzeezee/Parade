package com.g2t2.game;

import com.g2t2.types.PlayerProfile;

/**
 * Manages the current state of the application, specifically the active player profile.
 * Provides methods to set and retrieve the current player profile.
 * 
 * @author Oliver
 */
public class StateManager {
    private static PlayerProfile currentPlayerProfile = null;

    /**
     * Sets the current player profile and updates the application state accordingly.
     * After setting the profile, the start menu is displayed.
     * 
     * @param pp The {@link PlayerProfile} to be set as the current player profile.
     */
    public static void setPlayerProfile(PlayerProfile pp) {
        // After setting player profile, we know we which player to track games with
        currentPlayerProfile = pp;
    }

    /**
     * Retrieves the current player profile.
     * 
     * @return The {@link PlayerProfile} currently set in the application state, or {@code null} if none is set.
     */
    public static PlayerProfile getPlayerProfile() {
        return currentPlayerProfile;
    }
}
