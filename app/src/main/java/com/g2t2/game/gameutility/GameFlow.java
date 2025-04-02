package com.g2t2.game.gameutility;

import com.g2t2.game.GameState;
import com.g2t2.types.Player;
import com.g2t2.types.ScoreCalculation;

public class GameFlow {

    public static void startGame(GameState game) {
        game.giveOutInitialCards();
        game.startGame();
    }
    
    public static void processTurn(String[] indexOfChosenCard, GameState game) {
        Player playerThatJustPlayed = game.getCurrentPlayer();
        int cardIndex = Integer.parseInt(indexOfChosenCard[0]);
        GameProcessor.processPlayerTurn(cardIndex, playerThatJustPlayed, game);

        game.passTurnToNextPlayer();
        Player nextPlayer = game.getCurrentPlayer();
        
        if (game.isLastRound() && nextPlayer.equals(game.getPlayerWhoStartFinalRound())) { // everyone took 1 turn, go to put cards in collection phase
            game.setPut2CollectionRound(true);

        } else if (!game.isLastRound() && ScoreCalculation.meetsGameEndCondition(playerThatJustPlayed, game.getDeck())) { // go to last round
            game.setLastRound(true);
            game.setPlayerWhoStartFinalRound(nextPlayer);
        } 
    }

    public static void processPut2Collection(String[] indexOfChosenCard, GameState game) {
        Player playerThatJustPlayed = game.getCurrentPlayer();
        int[] cardIndices = {Integer.parseInt(indexOfChosenCard[0]), Integer.parseInt(indexOfChosenCard[1])};
        GameProcessor.processCardChosenToCollection(cardIndices, playerThatJustPlayed);

        game.passTurnToNextPlayer();
        Player nextPlayer = game.getCurrentPlayer();

        if (game.isPut2CollectionRound() && nextPlayer.equals(game.getPlayerWhoStartFinalRound())) { // go to score calculation
            game.setEndOfGame(true);
            ScoreCalculation.computeScore(game.getPlayers());
        }
    }
}
