package com.g2t2.game.gameutility;

import javax.swing.text.Utilities;

import com.g2t2.game.GameState;
import com.g2t2.network.networkUtility.NetworkUtility;
import com.g2t2.types.Player;
import com.g2t2.types.ScoreCalculation;

public class GameFlow {
    
    private void processTurn(String[] indexOfChosenCard, GameState game) {
        Player playerThatJustPlayed = game.getCurrentPlayer();
        int cardIndex = Integer.parseInt(indexOfChosenCard[1]);
        // System.out.println("current player: " + playerThatJustPlayed.getName());
        // System.out.println("CARD INDEX: " + cardIndex);
        GameProcessor.processPlayerTurn(cardIndex, playerThatJustPlayed, game);

        game.passTurnToNextPlayer();
        Player nextPlayer = game.getCurrentPlayer();
        //System.out.println("next player: " + nextPlayer.getName());
        
        if (game.isLastRound() && nextPlayer.equals(game.getPlayerWhoStartFinalRound())) { // everyone took 1 turn, go to put cards in collection phase
            game.setPut2CollectionRound(true);
            // System.out.println("Go to put 2 collection round");
            // System.out.println();

        } else if (!game.isLastRound() && ScoreCalculation.meetsGameEndCondition(playerThatJustPlayed, game.getDeck())) { // go to last round
            game.setLastRound(true);
            game.setPlayerWhoStartFinalRound(nextPlayer);
            // System.out.println("person that started final round: " + game.getPlayerWhoStartFinalRound().getName());
            // System.out.println();
        } 
        broadcastGameStatus(game);
    }

    private void processPut2Collection(String[] indexOfChosenCard, GameState game) {
        Player playerThatJustPlayed = game.getCurrentPlayer();
        int[] cardIndices = {Integer.parseInt(indexOfChosenCard[1]), Integer.parseInt(indexOfChosenCard[2])};
        // System.out.println("current player: " + playerThatJustPlayed.getName());
        // System.out.println("idx of card 1: " + cardIndices[0] + "|| idx of card 2: " + cardIndices[1]);
        GameProcessor.processCardChosenToCollection(cardIndices, playerThatJustPlayed);

        game.passTurnToNextPlayer();
        Player nextPlayer = game.getCurrentPlayer();
        // System.out.println("next player: " + nextPlayer.getName());

        if (game.isPut2CollectionRound() && nextPlayer.equals(game.getPlayerWhoStartFinalRound())) { // go to score calculation
            game.setEndOfGame(true);
            ScoreCalculation.computeScore(game.getPlayers());
        }
        broadcastGameStatus(game);
    }
}
