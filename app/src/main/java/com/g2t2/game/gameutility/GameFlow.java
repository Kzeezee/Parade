package com.g2t2.game.gameutility;

import com.g2t2.game.GameState;
import com.g2t2.types.Player;
import com.g2t2.types.ScoreCalculation;

/**
 * Manages the flow and state transitions of the game.
 * 
 * Handles key game flow processes including starting the game, processing 
 * player turns and handling the collection phase at the end of the game.
 */
public class GameFlow {

    /**
     * Initializes and starts a new game.
     * 
     * This method distributes initial cards to all players and sets the game state
     * to indicate that the game has started.
     * 
     * @param game the current game state to be initialized and started
     */
    public static void startGame(GameState game) {
        game.giveOutInitialCards();
        game.startGame();
    }
    
    /**
     * Processes a player's turn during the main game phase.
     * 
     * This method:
     * 1. Handles the card selection and placement for the current player
     * 2. Passes the turn to the next player
     * 3. Checks if game end conditions are met:
     *    - If in the last round and it returns to the player who started the final round,
     *      transitions to the "put cards in collection" phase
     *    - If a player meets the game end condition, sets the game to the last round
     * 
     * @param indexOfChosenCard array containing the index of the card chosen by the player
     * @param game the current game state
     */
    public static void processTurn(String[] indexOfChosenCard, GameState game) {
        Player playerThatJustPlayed = game.getCurrentPlayer();
        int cardIndex = Integer.parseInt(indexOfChosenCard[0]);
        GameProcessor.processPlayerTurn(cardIndex, playerThatJustPlayed, game);

        game.passTurnToNextPlayer();
        Player nextPlayer = game.getCurrentPlayer();
        
        if (game.isLastRound() && nextPlayer.equals(game.getPlayerWhoStartFinalRound())) { // everyone took 1 turn, go to put cards in collection phase
            game.setPut2CollectionRound(true);
            // create the frozen collection
            for (Player player : game.getPlayers()) {
                player.setFinalCardsInCollection();
            }

        } else if (!game.isLastRound() && ScoreCalculation.meetsGameEndCondition(playerThatJustPlayed, game.getDeck())) { // go to last round
            game.setLastRound(true);
            game.setPlayerWhoStartFinalRound(nextPlayer);
        } 
    }

    /**
     * Processes a player's selection of cards to add to their collection during the final phase.
     * 
     * This method:
     * 1. Handles the player selecting two cards from their hand to add to their collection
     * 2. Passes the turn to the next player
     * 3. Checks if all players have completed this phase:
     *    - If the turn returns to the player who started the final round,
     *      transitions to game end and calculates final scores
     * 
     * @param indexOfChosenCard array containing the indices of the two cards chosen by the player
     * @param game the current game state
     */
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
