package com.g2t2.game;

import com.g2t2.types.Player;
import com.g2t2.types.PlayerProfile;
import com.g2t2.util.Constants;
import com.g2t2.util.Display;
import com.g2t2.util.Utility;
import com.g2t2.db.dao.impl.PlayerProfileDaoImpl;
import com.g2t2.game.gameInit.GameInit;
import com.g2t2.game.gameutility.CPUGameAction;
import com.g2t2.game.gameutility.GameFlow;
import com.g2t2.game.gameutility.GameRenderer;
import com.g2t2.game.gameutility.GamePlayPrompt;

import java.util.Comparator;
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
     * @param game The current game state.
     */
    public static void startGame(GameState game) {
        GameFlow.startGame(game);   
        List<Player> players = game.getPlayers();
        if (game.getCurrentPlayer().getCPU()) { // starting player is a bot
            GameRenderer.displayRoundInformation(game);
        }

        while (!game.isPut2CollectionRound()) {
            String cardChosen = null;
            if(!game.getCurrentPlayer().getCPU()){
                Utility.clearConsoleScreen();
                GameRenderer.temporaryPauseScreen("It is " + game.getCurrentPlayer().getName() + "'s turn. Press the enter key to continue");
                GameRenderer.displayRoundInformation(game);
                GameRenderer.displayHandCards(game.getCurrentPlayer());
                cardChosen = GamePlayPrompt.promptForCard(game.getCurrentPlayer(), Constants.PROMPT_ADD_TO_PARADE);
            } else {
                cardChosen = CPUGameAction.CPUSelectCard(game.getCurrentPlayer(), game);
            }
            if (cardChosen.equals(Constants.LEAVE_GAME)){
                GameInit.displayGameOptions();
            }
            GameFlow.processTurn(new String[]{cardChosen}, game);
        }

        while(!game.isEndOfGame()){
            String[] cardsChosen = null;
            if(!game.getCurrentPlayer().getCPU()){
                Utility.clearConsoleScreen();
                GameRenderer.temporaryPauseScreen("It is " + game.getCurrentPlayer().getName() + "'s turn. Press the enter key to continue");
                GameRenderer.displayRoundInformation(game);
                GameRenderer.displayHandCards(game.getCurrentPlayer());
                cardsChosen = GamePlayPrompt.promptToCollectTwo(game.getCurrentPlayer());
            } else {
                cardsChosen = CPUGameAction.CPUSelectTwoToCollections(game.getCurrentPlayer(), game);
            }
            if (cardsChosen[0].equals(Constants.LEAVE_GAME)){
                GameInit.displayGameOptions();
            }
            GameFlow.processPut2Collection(cardsChosen, game);
        }

        Display.gameHeader();
        GameRenderer.displayScores(players);

        updatePlayerWinsInDB(players);

        if (GamePlayPrompt.promptForNewGame()) {
            Player.resetCounter();
            GameInit.displayGameOptions();
        } else {
            StartMenu.showStartMenu();
        }
    }

    /**
     * Updates the number of wins that the player with the
     * highest score have, and save it to the database.
     * @param players
     */
    private static void updatePlayerWinsInDB(List<Player> players) {
        List<Player> sortedPlayers = players.stream()
                .sorted(Comparator.comparingInt(Player::getScore))
                .toList();
        PlayerProfile winnerProfile = sortedPlayers.get(0).getPlayerProfile();
        if (winnerProfile != null) {
            System.out.println(winnerProfile.getName());
            winnerProfile.setWins(winnerProfile.getWins() + 1);
            new PlayerProfileDaoImpl().updatePlayerProfile(winnerProfile);
        }
    }
}