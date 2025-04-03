package com.g2t2.network.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.*;

import com.g2t2.game.*;
import com.g2t2.game.gameutility.*;
import com.g2t2.network.networkUtility.*;
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

    private void createGame(String name) throws IOException {
        GameState game = new GameState();
        gameId = game.getGameId();
        clientHandlers.put(gameId, new ArrayList<>());
        availableGames.put(gameId, game);
        createPlayer(name, game);
    }

    private void createPlayer(String name, GameState game) throws IOException {
        Player player = new Player(name);
        player.setPlayerProfile(StateManager.getPlayerProfile());
        game.addPlayer(player);
        clientHandlers.get(gameId).add(this);
        myPlayerId = player.getPlayerId();
        NetworkUtility.sendPlayer(player, objectOutputStream);
        NetworkUtility.sendGameState(game, objectOutputStream);
        broadcastMessage(new String[] {Constants.REFRESH_PAGE});
    }

    private void createCPU(int numOfCPU, int difficulty, GameState game) {
        for (int i = 0; i < numOfCPU; i++ ) {
        Player cpu = new Player(true, difficulty);
        game.addPlayer(cpu);
        }
    }

    private void removeCPU(String[] ids, GameState game) {
        for (int i = 0; i < ids.length - 1; i++) {
            game.removePlayer(NetworkUtility.findMyPlayer(game, Integer.parseInt(ids[i])));
        }
    }

    private void processPlayerLeaveLobby(GameState game) {
        game.removePlayer(NetworkUtility.findMyPlayer(game, myPlayerId));
        if (!game.setNewGameMaster()) {
            availableGames.remove(gameId);
        }
        removePlayerFromGame();
    }
    
    private void processPlayerLeaveGame(GameState game) {
        NetworkUtility.findMyPlayer(game, myPlayerId).setCPU(true);
        if (!game.setNewGameMaster()) {
            availableGames.remove(gameId);
        }
        removePlayerFromGame();
    }

    private void resetGame(GameState game) {
        game.resetGame();
        clientHandlers.get(gameId).clear();
    }

    // This method contains everything that is supposed to ran on a separate thread, such as blocking operations
    @Override
    public void run() {
        while (socket.isConnected()) {
            try {
                Object incomingObject = objectInputStream.readObject();
                if (incomingObject instanceof Message messageToProcess) {
                    String command = messageToProcess.getMessage()[messageToProcess.getMessage().length - 1];
                    System.out.println("command = " + command);
                    System.out.println("gameid = " + gameId);
                    GameState game = null;
                    if (gameId != null) {
                        game = NetworkUtility.findGameState(gameId, availableGames);
                    }

                    if (command.equals(Constants.CREATE_GAME)) { // create game
                        createGame(messageToProcess.getMessage()[0]);
                    
                    } else if (command.equals(Constants.JOIN_GAME)) { // join game
                        // checking if gameId sent in is valid
                        game = NetworkUtility.findGameState(messageToProcess.getMessage()[1], availableGames);
                        if (game == null) {
                            NetworkUtility.sendMessage(new String[] {Constants.INVALID_GAME_ID}, objectOutputStream);
                        } else if (game.getNumOfPlayers() >= 6) {
                            NetworkUtility.sendMessage(new String[] {Constants.FULL_CAPACITY}, objectOutputStream);
                        }
                        else {
                            gameId = messageToProcess.getMessage()[1];
                            createPlayer(messageToProcess.getMessage()[0], game);
                        }

                    } else if (command.equals(Constants.ADD_CPU)) { // add cpu
                        createCPU(Integer.parseInt(messageToProcess.getMessage()[0]), Integer.parseInt(messageToProcess.getMessage()[1]), game);
                        NetworkUtility.sendGameState(game, objectOutputStream);
                        broadcastMessage(new String[] {Constants.REFRESH_PAGE});

                    } else if (command.equals(Constants.REMOVE_CPU)) { // remove cpu
                        removeCPU(messageToProcess.getMessage(), game);
                        NetworkUtility.sendGameState(game, objectOutputStream);
                        broadcastMessage(new String[] {Constants.REFRESH_PAGE});

                    } else if (command.equals(Constants.START_GAME)) { // start game
                        GameFlow.startGame(game);
                        broadcastGameStatus(game);

                    } else if (command.equals(Constants.LEAVE_GAME)) { // leave game
                        if (game.getHasStarted()) { // leave game
                            processPlayerLeaveGame(game);
                        } else { // leave from lobby
                            processPlayerLeaveLobby(game);
                        }
                        NetworkUtility.sendMessage(new String[] {Constants.NEW_GAME}, objectOutputStream);

                    }  else if (command.equals(Constants.NEW_GAME)) { // player wants to play again
                        createPlayer(messageToProcess.getMessage()[0], game);

                    } else if (command.equals(Constants.COMPLETE_TURN) && game != null) { // player finish his turn
                        GameFlow.processTurn(messageToProcess.getMessage(), game);
                        broadcastGameStatus(game);

                    } else if (command.equals(Constants.PUT_TO_COLLECTION) && game != null) { // put 2 collection phase
                        GameFlow.processPut2Collection(messageToProcess.getMessage(), game);
                        broadcastGameStatus(game);

                    } else if (command.equals(Constants.ENTER_KEY)) { // refresh page for that player only
                        NetworkUtility.sendGameState(game, objectOutputStream); 
                    
                    } else if (command.equals(Constants.GAME_ENDED)) { // reset game setting
                        resetGame(game);

                    } else if (command.equals(Constants.CLOSE_APP)) {
                        throw new IOException();
                    }
                }
            } catch (IOException e) {
                closeEverything(socket, objectInputStream, objectOutputStream);
                break; // break out from the while loop
            } catch (ClassNotFoundException e) {
                closeEverything(socket, objectInputStream, objectOutputStream);
                break;
            }
        }
    }
    
    // send gameState to all players in the game
    private void broadcastGameStatus(GameState game) throws IOException {
        for (ClientHandler clientHandler : clientHandlers.get(gameId)) {
            NetworkUtility.sendGameState(game, clientHandler.objectOutputStream);
        }
    }

    // broadcoast a message to all players less the sender
    private void broadcastMessage(String[] message) throws IOException {
        for (ClientHandler clientHandler : clientHandlers.get(gameId)) {
            if (clientHandler.myPlayerId != this.myPlayerId) {
                NetworkUtility.sendMessage(message, clientHandler.objectOutputStream);
            }
        }
    } 

    private void removePlayerFromGame() {
        if (gameId != null) {
            clientHandlers.get(gameId).remove(this);
        }
        myPlayerId = 0;
        gameId = null;
    }

    private void closeEverything(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOuputStream) {
        removePlayerFromGame();
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
            System.out.println("client has disconnected");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
