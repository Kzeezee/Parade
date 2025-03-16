package com.g2t2.types;

import java.util.Comparator;

/**
 * Comparator for the player based on descending score, if same score, sort by ascending name
 * as well as to compute score for the players after the game ends
 * 
 * @author Shwe
 */
public class PlayerComparator implements Comparator<Player>{
    public int compare(Player p1, Player p2) {
        int cmp = p1.getScore() - p2.getScore();
        if (cmp != 0) {
            return cmp;
        }
        cmp = p1.getName().compareTo(p2.getName());
        return cmp;
    }
}
