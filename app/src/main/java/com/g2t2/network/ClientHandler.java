package com.g2t2.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

import com.g2t2.game.*;
import com.g2t2.game.gameutility.*;
import com.g2t2.network.networkUtility.NetworkUtility;
import com.g2t2.util.*;
import com.g2t2.types.*;

public class ClientHandler implements Runnable { // allows instances to be executed by a separate thread
    
    // keep track of all the clients - to send msg to all of them (broadcast to all)
    public static Map<String, List<ClientHandler>> clientHandlers = new HashMap<>();
    public static Map<String, GameState> availableGames = new HashMap<>();
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String gameId;  // used to check what game the player is playing
    private int myPlayerId; // identify the clientHandler

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        }
    }

    private void createGame(String name) {
        GameState game = new GameState();
        gameId = game.getGameId();
        clientHandlers.put(gameId, new ArrayList<>());
        availableGames.put(gameId, game);
        createPlayer(name);
    }

    private void createPlayer(String name) {
        GameState game = NetworkUtility.findGameState(gameId, availableGames);
        Player player = new Player(name);
        player.setPlayerProfile(StateManager.getPlayerProfile());
        game.addPlayer(player);
        clientHandlers.get(gameId).add(this);
        myPlayerId = player.getPlayerId();
        sendPlayer(player);
        sendGameState(game);
        broadcastMessage(new String[] {Constants.REFRESH_PAGE});
    }

    private void createCPU(int numOfCPU, int difficulty) {
        GameState game = NetworkUtility.findGameState(gameId, availableGames);
        for (int i = 0; i < numOfCPU; i++ ) {
        Player cpu = new Player(true, difficulty);
        game.addPlayer(cpu);
        }
        sendGameState(game);
        broadcastMessage(new String[] {Constants.REFRESH_PAGE});
    }

    private void processPlayerLeaveLobby(GameState game) {
        game.removePlayer(NetworkUtility.findMyPlayer(game, myPlayerId));
        removeClientHandler();
        if (game.getNumOfPlayers() == 1 || !game.setNewGameMaster()) {
            availableGames.remove(gameId);
        }
    }
    
    private void processPlayerLeaveGame(GameState game) {
        NetworkUtility.findMyPlayer(game, myPlayerId).setCPU(true);
        NetworkUtility.findMyPlayer(game, myPlayerId).
        removeClientHandler();
        if (!game.setNewGameMaster()) {
            availableGames.remove(gameId);
        }
    }

    private void startGame() {
        GameState game = NetworkUtility.findGameState(gameId, availableGames);
        game.giveOutInitialCards();
        game.startGame();
        broadcastGameStatus(game);
    }

    private void processTurn(String[] indexOfChosenCard) {
        GameState game = NetworkUtility.findGameState(gameId, availableGames);
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

    private void processPut2Collection(String[] indexOfChosenCard) {
        GameState game = NetworkUtility.findGameState(gameId, availableGames);
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

    // This method contains everything that is supposed to ran on a separate thread, such as blocking operations
    @Override
    public void run() {
        while (socket.isConnected()) {
            try {
                Object incomingObject = objectInputStream.readObject();
                if (incomingObject instanceof Message messageToProcess) {
                    String command = messageToProcess.getMessage()[messageToProcess.getMessage().length - 1];

                    if (command.equals(Constants.CREATE_GAME)) { // create game
                        createGame(messageToProcess.getMessage()[0]);
                    
                    } else if (command.equals(Constants.JOIN_GAME)) { // join game
                        // checking if gameId sent in is valid
                        GameState game = NetworkUtility.findGameState(messageToProcess.getMessage()[1], availableGames);
                        if (game == null) {
                            sendMessage(new String[] {Constants.INVALID_GAME_ID});
                        } else if (game.getNumOfPlayers() >= 6) {
                            sendMessage(new String[] {Constants.FULL_CAPACITY});
                        }
                        else {
                            gameId = messageToProcess.getMessage()[1];
                            createPlayer(messageToProcess.getMessage()[0]);
                        }

                    } else if (command.equals(Constants.ADD_CPU)) { // add cpu
                        createCPU(Integer.parseInt(messageToProcess.getMessage()[0]), Integer.parseInt(messageToProcess.getMessage()[1]));

                    } else if (command.equals(Constants.START_GAME)) { // start game
                        startGame();

                    } else if (command.equals(Constants.LEAVE_GAME)) { // leave game
                        GameState game = NetworkUtility.findGameState(gameId, availableGames);
                        if (game.getHasStarted()) { // leave game
                            processPlayerLeaveGame(game);
                        } else { // leave from lobby
                            processPlayerLeaveLobby(game);
                        }
                        sendMessage(new String[] {Constants.NEW_GAME});

                    }  else if (command.equals(Constants.NEW_GAME)) { // player wants to play again
                        createPlayer(messageToProcess.getMessage()[0]);

                    } else if (command.equals(Constants.COMPLETE_TURN)) { // player finish his turn
                        processTurn(messageToProcess.getMessage());

                    } else if (command.equals(Constants.PUT_TO_COLLECTION)) { // put 2 collection phase
                        processPut2Collection(messageToProcess.getMessage());

                    } else if (command.equals(Constants.ENTER_KEY)) { // refresh page for that player only
                        sendGameState(NetworkUtility.findGameState(gameId, availableGames)); 
                    }
                }
            } catch (IOException e) {
                System.out.println("Something happened----");
                closeEverything(socket, objectInputStream, objectOutputStream);
                break; // break out from the while loop, because client socket is still connected
            } catch (ClassNotFoundException e) {
                closeEverything(socket, objectInputStream, objectOutputStream);
                break;
            }
        }
    }
    
    // send gameState to all players in the game
    private void broadcastGameStatus(GameState game) {
        // System.out.println("Broadcasting GameState: " + game.getGameId());
        for (ClientHandler clientHandler : clientHandlers.get(gameId)) {
            clientHandler.sendGameState(game);
        }
    }

    // broadcoast a message to all players less the sender
    private void broadcastMessage(String[] message) {
        // System.out.println("Broadcasting Message");
        for (ClientHandler clientHandler : clientHandlers.get(gameId)) {
            if (clientHandler.myPlayerId != this.myPlayerId) {
                // System.out.println("Sending to clientHandler: " + clientHandler.myPlayerId);
                clientHandler.sendMessage(message);
            }
        }
    } 

    private void sendGameState(GameState game) {
        GameState snapshot = NetworkUtility.findGameState(gameId, availableGames).deepCopy(); // Create a snapshot
        try {
            objectOutputStream.writeObject(snapshot);
            objectOutputStream.flush();
        } catch (IOException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        }
    }

    private void sendPlayer(Player player) {
        try {
            objectOutputStream.writeObject(player);
            objectOutputStream.flush();
        } catch (IOException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        }
    }

    private void sendMessage(String message[]) {
        try {
            objectOutputStream.writeObject(new Message(message));
            objectOutputStream.flush();
        } catch (IOException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        }
    }

    private void removeClientHandler() {
        clientHandlers.get(gameId).remove(this);
    }

    private void closeEverything(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOuputStream) {
        removeClientHandler();
        try {
            if (objectInputStream != null) {
                objectInputStream.close(); // closing the outer wrapper will close all inner wrapers
            }
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
