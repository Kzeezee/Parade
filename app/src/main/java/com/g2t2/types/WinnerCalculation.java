// package com.g2t2.types;

// import java.util.*;

// public class WinnerCalculation {

//     /**
//      * Determines the winner (player with the lowest score).
//      * author @chue
//      */

//     public static Player findWinner(Map<Player, Integer> scoreMap) {

//         Player winner = null;
//         int lowestScore = Integer.MAX_VALUE;
        
//         for (Map.Entry<Player, Integer> entry : scoreMap.entrySet()) {

//             if (entry.getValue() < lowestScore) {
//                 lowestScore = entry.getValue();
//                 winner = entry.getKey();
//             }
//         }
        
//         return winner;
//     }
// }
