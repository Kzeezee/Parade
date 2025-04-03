package com.g2t2.network.client;

import com.g2t2.game.GameState;
import com.g2t2.game.gameutility.CPUGameAction;
import com.g2t2.game.gameutility.GamePlayPrompt;
import com.g2t2.types.Player;
import com.g2t2.util.Constants;

public class OnlineGameProcessor {
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
                    caller.setIsInGame(false);
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
            String indexOfChosenCard = GamePlayPrompt.promptForCard(currentPlayer, Constants.PROMPT_ADD_TO_PARADE);
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
