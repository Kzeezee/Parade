package com.g2t2.game;

import java.util.List;

import com.g2t2.types.PlayerProfile;
import com.g2t2.util.Constants;
import com.g2t2.util.Utility;

public class MenuRenderer {
    public static void displayPlayerProfiles(List<PlayerProfile> players) {
        Utility.clearConsoleScreen();
        System.out.println(Constants.ANSI_RESET);
        System.out.println(Constants.BANNER);
        System.out.println(Constants.DIVIDER);

        // If it isn't empty, display selection of all the profiles.
        for (int i = 0; i < players.size(); i++) {
            System.out.printf(" 📌 %s | %d wins | (%d)\n", players.get(i).getName(), players.get(i).getWins(),
                    i + 1);
        }
        System.out.println("\n\n\n");
        System.out.println(Constants.DIVIDER);
    }
}
