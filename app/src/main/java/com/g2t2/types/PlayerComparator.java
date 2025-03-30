package com.g2t2.types;

import java.util.Comparator;

/**
 * Comparator for the player.
 * 
 * @author Shwe
 */
public class PlayerComparator implements Comparator<Player>{
    /**
     * Compares two players in the order of:
     * 1. sort by ascending score
     * 2. sort by ascending no of cards
     * 2. sort by ascending name
     * 
     * @param p1 first player to be compared
     * @param p2 second player to be compared
     * @return a negative integer if p1 should come before p2, a positive integer if p1 should 
     *         come after p2, or 0 if they are considered equal based on score and name
     */
    public int compare(Player p1, Player p2) {
        int cmp = p1.getScore() - p2.getScore();
        if (cmp != 0) {
            return cmp;
        }
        cmp = p1.getCollectionSize() - p2.getCollectionSize();
        if (cmp != 0) {
            return cmp;
        }
        cmp = p1.getName().compareTo(p2.getName());
        return cmp;
    }
}
