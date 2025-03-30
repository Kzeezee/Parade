package com.g2t2.game;

import com.g2t2.types.Player;
import com.g2t2.types.ScoreCalculation;

import com.g2t2.game.GameUtility.GameProcessor;
import com.g2t2.game.GameUtility.GameRenderer;
import com.g2t2.game.GameUtility.Prompt;

import java.util.List;

/**
 * Class responsible for managing the gameplay flow.
 * 
 * This class handles the main game loop, turn management, end-game conditions,
 * and score calculation. It ensures that all players take their turns in order
 * and manages the transition between game phases, such as the last round of
 * play
 * and final scoring.
 * 
 * @author Ben
 * @author Shwe
 * @author Easan
 */
public class GamePlay {
    /**
     * Starts the game and manages the main gameplay loop.
     * 
     * This method initializes the game state, selects a random player to start,
     * and iterates through player turns until an end-game condition is met. Once
     * the game ends, it triggers the final round of play, allows players to choose
     * two cards to add to their collections, calculates scores, and displays the
     * results.
     *
     * @param game the current game state
     */
    public static void startGame(GameState game) {
        game.setHasStarted(true);
        List<Player> players = game.getPlayers();

        // finding a random person to start the game
        int currentIdx = (int) (Math.random() * players.size());
        int previousIdx = currentIdx;

        // checks the previous player because they are the one that might have a
        // complete collection (ALL COLOURS)
        while (!ScoreCalculation.meetsGameEndCondition(players.get(previousIdx), game.getDeck())) {
            game.setCurrentPlayer(players.get(currentIdx));
            GameProcessor.playTurn(players.get(currentIdx), game);
            previousIdx = currentIdx;
            currentIdx = (currentIdx + 1) % players.size();
        }

        lastRoundOfPlay(players, currentIdx, game);

        chooseTwoCardsToCollection(players, currentIdx, game);

        ScoreCalculation.computeScore(players);
        GameRenderer.displayScores(players);

        Prompt.promptForNewGame();
    }

    /**
     * Executes the last round of play after an end-game condition is met.
     * 
     * Each player takes one final turn in order starting from the current player.
     *
     * @param players the list of all players in the game
     * @param idx     the index of the player who starts the last round
     * @param game    the current game state
     */
    private static void lastRoundOfPlay(List<Player> players, int idx, GameState game) {
        game.setIsLastRound(true);
        for (int i = 0; i < players.size(); i++) {
            GameProcessor.playTurn(players.get(idx), game);
            idx = (idx + 1) % players.size();
        }
    }

    /**
     * Allows each player to select two cards from their hand to add to their collection.
     *
     * This phase occurs after the last round of play and before final scoring.
     *
     * @param players the list of all players in the game
     * @param idx     the index of the player who starts this phase
     * @param game    the current game state
     */
    private static void chooseTwoCardsToCollection(List<Player> players, int idx, GameState game) {
        for (int i = 0; i < players.size(); i++) {
            GameProcessor.putTwoToCollections(players.get(idx), game);
            idx = (idx + 1) % players.size();
        }
    }
}