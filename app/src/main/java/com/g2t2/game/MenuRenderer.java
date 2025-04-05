package com.g2t2.game;

import java.util.List;

import com.g2t2.types.PlayerProfile;
import com.g2t2.util.Constants;
import com.g2t2.util.Utility;

/**
 * Utility class responsible for rendering menu-related UI elements.
 */
public class MenuRenderer {
    /**
     * Displays a list of player profiles with their names and win counts.
     * 
     * This method formats and presents player profile information in a numbered list 
     * format. Each player entry shows:
     * - A sequential number for selection
     * - The player's name
     * - The number of wins the player has accumulated
     * 
     * @param players the list of PlayerProfile objects to be displayed
     */
    public static void displayPlayerProfiles(List<PlayerProfile> players) {
        Utility.clearConsoleScreen();
        System.out.println(Constants.ANSI_RESET);
        System.out.println(Constants.BANNER);
        System.out.println(Constants.DIVIDER);

        // If it isn't empty, display selection of all the profiles.
        for (int i = 0; i < players.size(); i++) {
            System.out.printf(" (%d) %s | %d wins\n", i + 1, players.get(i).getName(), players.get(i).getWins());
        }
        System.out.println("\n\n");
        System.out.println(Constants.DIVIDER);
    }
}
