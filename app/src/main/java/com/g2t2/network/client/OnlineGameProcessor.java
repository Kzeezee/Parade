package com.g2t2.network.client;

import com.g2t2.game.GameState;
import com.g2t2.game.gameutility.CPUGameAction;
import com.g2t2.game.gameutility.GamePlayPrompt;
import com.g2t2.types.Player;
import com.g2t2.util.Constants;

/** 
 * This OnlineGameProcessor class handles turn-based game actions 
 * for both CPU and human players in an online multiplayer environment. 
 * 
 * It determines the correct behaviour depending on the player type (CPU or human)
 * and whether it is the current player's turn. It also ensures the gameMaster 
 * is the only one responsible for sending updates to the server during CPU turns.
 *
 * @author Ben
 */
public class OnlineGameProcessor {
    /**
     * Processes a game round where a player must put two cards into their collection.
     * 
     * If the current player is a CPU, the {@link CPUGameAction} handles the logic 
     * for selecting the cards, and the gameMaster sends the result to the server.
     * 
     * If the player is a human, they are prompted to choose two cards, unless they 
     * leave the game, in which case a bot takes over the decision.
     *
     * @param currentPlayer the player whose turn it is
     * @param game the current game
     * @param myPlayerId the ID of the player running this client
     * @param caller the client instance to update in game status
     * @return a String array containing indices of the two selected card indices and their action command, 
     *         otherwise null if not their turn
     */
    public static String[] processPut2CollectionRound(Player currentPlayer, GameState game, int myPlayerId, Client caller) {
        String[] messageToSendBack = {"", "", Constants.PUT_TO_COLLECTION};
        if (currentPlayer.getCPU()) { // CPU turn
            CPUGameAction.displayCPUTurnAction(currentPlayer);
            if (game.getGameMaster().getPlayerId() == myPlayerId) { // only gameMaster sends back to server
                String[] indexOfChosenCard = CPUGameAction.CPUSelectTwoToCollections(currentPlayer, game);
                messageToSendBack[0] = indexOfChosenCard[0];
                messageToSendBack[1] = indexOfChosenCard[1];
            } else { // the rest don't do anything
                System.out.println("It is " + currentPlayer.getName() + "'s turn. Not Your Turn.");
                return null;
            }
        } else if (currentPlayer.getPlayerId() == myPlayerId) { // your turn
                String[] indexOfChosenCard = GamePlayPrompt.promptToCollectTwo(currentPlayer);
                if (indexOfChosenCard[0].equals(Constants.LEAVE_GAME)) {
                    caller.setIsInGame(false); // client no longer isInGame
                    // bot to take over the turn
                    String[] temp = CPUGameAction.CPUSelectTwoToCollections(currentPlayer, game);
                    indexOfChosenCard[0] = temp[0];
                    indexOfChosenCard[1] = temp[1];
                }
                messageToSendBack[0] = indexOfChosenCard[0];
                messageToSendBack[1] = indexOfChosenCard[1];
        } else {
            System.out.println("It is " + currentPlayer.getName() + "'s turn. Not Your Turn.");
            return null;
        }
        return messageToSendBack;
    }

    /**
     * Processes a normal game round where a player adds a card to the parade.
     *
     * If the current player is a CPU, the {@link CPUGameAction} selects the card
     * to be played, and the gameMaster sends the result to the server.
     * 
     * If the player is a human, they are prompted for their card choice,
     * or if they leave the game, a bot makes the choice on their behalf.
     *
     * @param currentPlayer the player whose turn it is
     * @param game the current game
     * @param myPlayerId the ID of the player running this client
     * @param caller the client instance to update game status
     * @return a String array containing the index of the selected card and their action command
     *         otherwise null if not their turn
     */
    public static String[] processNormalRound(Player currentPlayer, GameState game, int myPlayerId, Client caller) {
        String[] messageToSendBack = {"", "", Constants.COMPLETE_TURN};
        if (currentPlayer.getCPU()) { // CPU turn
            CPUGameAction.displayCPUTurnAction(currentPlayer);
            if (game.getGameMaster().getPlayerId() == myPlayerId) { // only gameMaster sends back to server
                int indexOfChosenCard = CPUGameAction.chooseCPUCardIdx(currentPlayer, game, -1);
                messageToSendBack[0] = indexOfChosenCard + "";
            } else { // the rest don't do anything
                System.out.println("It is " + currentPlayer.getName() + "'s turn. Not Your Turn.");
                return null;
            }
        } else if (currentPlayer.getPlayerId() == myPlayerId) { // your turn
            String indexOfChosenCard = GamePlayPrompt.promptForCard(currentPlayer, Constants.ADD_TO_PARADE_PROMPT);
            if (indexOfChosenCard.equals(Constants.LEAVE_GAME)) {
                caller.setIsInGame(false);
                // bot to take over the turn
                indexOfChosenCard = CPUGameAction.chooseCPUCardIdx(currentPlayer, game, -1) + "";
            }
            messageToSendBack[0] = indexOfChosenCard; 
        } else {
            System.out.println("It is " + currentPlayer.getName() + "'s turn. Not Your Turn.");
            return null;
        }
        return messageToSendBack;
    }
}
