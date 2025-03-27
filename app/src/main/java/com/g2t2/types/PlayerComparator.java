package com.g2t2.types;

import java.util.Comparator;

/**
 * Comparator for the player
 * 1. sort by descending score
 * 2. sort by ascending name
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
