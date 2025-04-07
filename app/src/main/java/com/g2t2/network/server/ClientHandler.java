package com.g2t2.network.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.g2t2.game.GameState;
import com.g2t2.game.gameInit.GameInit;
import com.g2t2.game.gameutility.GameFlow;
import com.g2t2.network.networkUtility.Message;
import com.g2t2.network.networkUtility.NetworkUtility;
import com.g2t2.types.Player;
import com.g2t2.util.Constants;

/**
 * This ClientHandler class acts as the server for client.
 * Each instance of the client will be communicating to an instance of a clientHandler. 
 * 
 * This class represents the server and will update the GameState of the current game that their client is in
 * and send it back to the client for them to render it on the player's screen.
 * Each clientHandler has the ability to talk to all other clientHandlers to ensure that all the clients
 * that are playing the same game are receiving the same state of the game.
 * 
 * All creation and deletion of players / cpu are done on the server side.
 * 
 * Each instance of a clientHandler is running on its own thread
 * 
 * @author Ben
 */
public class ClientHandler implements Runnable {
    
    // keep track of all the clients in a game
    public static Map<String, List<ClientHandler>> clientHandlers = new HashMap<>();
    // keep track of the currently available games in the server
    public static Map<String, GameState> availableGames = new HashMap<>();
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String gameId;  // used to identify what game the client is playing
    private int myPlayerId; // used to identify the player of the client

    /**
     * ClientHandler Constructor.
     * Each clientHandler needs to have an object out/inputstream to communicate with the client
     *
     * @param socket represents the connection between the server and the client
     * @param username name of the client (chosen from the player profile)
     */
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        }
    }

    /**
    * Main logic thread for listening to messages from the client.
    * It reads objects from the input stream and processes them based on their command type.
    * Handles commands for creating/joining games, starting the game, player actions, and disconnection.
    */
    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                Object incomingObject = objectInputStream.readObject();
                if (incomingObject instanceof Message messageToProcess) {
                    // the last index of Message always contains the purpose of the Message
                    String command = messageToProcess.getMessage()[messageToProcess.getMessage().length - 1];
                    GameState game = null;
                    if (gameId != null) { // true if client is currently in a game
                        game = NetworkUtility.findGameState(gameId, availableGames);
                    }

                    if (command.equals(Constants.CREATE_GAME)) { // clients want to create a game
                        createGame(messageToProcess.getMessage()[0]); // contains the client's name
                        createPlayer(messageToProcess.getMessage()[0], NetworkUtility.findGameState(gameId, availableGames));
                    
                    } else if (command.equals(Constants.JOIN_GAME)) { // clients wants to join a game
                        // checking if gameId sent in is valid
                        game = NetworkUtility.findGameState(messageToProcess.getMessage()[1], availableGames);

                        if (game == null) {
                            NetworkUtility.sendMessage(new String[] {Constants.INVALID_GAME_ID}, objectOutputStream);
                        } else if (game.getNumOfPlayers() >= 6) {
                            NetworkUtility.sendMessage(new String[] {Constants.FULL_CAPACITY}, objectOutputStream);
                        }
                        else { 
                            gameId = messageToProcess.getMessage()[1]; // contains the gameID
                            createPlayer(messageToProcess.getMessage()[0], game);
                        }

                    } else if (command.equals(Constants.ADD_CPU)) { // gameMaster wants to add cpu
                        GameInit.createCPU(Integer.parseInt(messageToProcess.getMessage()[0]), Integer.parseInt(messageToProcess.getMessage()[1]), game);
                        NetworkUtility.sendGameState(game, objectOutputStream);
                        broadcastMessage(new String[] {Constants.REFRESH_PAGE}); // all other players will refresh their page

                    } else if (command.equals(Constants.REMOVE_CPU)) { // gameMaster wants to remove cpu
                        GameInit.removePlayers(messageToProcess.getMessage(), game);
                        NetworkUtility.sendGameState(game, objectOutputStream);
                        broadcastMessage(new String[] {Constants.REFRESH_PAGE}); // all other players will refresh their page

                    } else if (command.equals(Constants.START_GAME)) { // gameMaster wants to start game
                        GameFlow.startGame(game); // settles the start game procedure
                        broadcastGameStatus(game);

                    } else if (command.equals(Constants.LEAVE_GAME)) { // player want leave the game
                        if (game.getHasStarted()) { // leave from the game
                            processPlayerLeaveGame(game);
                        } else { // leave from the lobby
                            processPlayerLeaveLobby(game);
                            broadcastMessage(new String[] {Constants.REFRESH_PAGE}); // all other players will refresh their page
                        }
                        removePlayerFromGame();
                        NetworkUtility.sendMessage(new String[] {Constants.NEW_GAME}, objectOutputStream);

                    }  else if (command.equals(Constants.NEW_GAME)) { // player wants to play again
                        createPlayer(messageToProcess.getMessage()[0], game);   

                    } else if (command.equals(Constants.COMPLETE_TURN)) { // player finish his turn (normal phase)
                        GameFlow.processTurn(messageToProcess.getMessage(), game);
                        broadcastGameStatus(game);

                    } else if (command.equals(Constants.PUT_TO_COLLECTION)) { // player finish his turn (put 2 collection phase)
                        GameFlow.processPut2Collection(messageToProcess.getMessage(), game);
                        broadcastGameStatus(game);

                    } else if (command.equals(Constants.ENTER_KEY)) { // refresh page for that player only
                        NetworkUtility.sendGameState(game, objectOutputStream); 
                    
                    } else if (command.equals(Constants.GAME_ENDED)) { // reset game setting after it ends
                        resetGame(game);

                    } else if (command.equals(Constants.GO_BACK) || command.equals(Constants.CLOSE_APP)) { // close clientHandler
                        throw new IOException();
                    }
                }
            } catch (IOException e) {
                closeEverything(socket, objectInputStream, objectOutputStream);
            } catch (ClassNotFoundException e) {
                closeEverything(socket, objectInputStream, objectOutputStream);
            }
        }
    }

    /**
     * Creates a new game by generating an instance of GameState
     * Adds the game to the list of available games and initialises a list of client handlers for that game.
     *
     * @param name name of the client (chosen from the player profile)
     */
    private void createGame(String name) {
        GameState game = new GameState();
        gameId = game.getGameId();
        clientHandlers.put(gameId, new ArrayList<>());
        availableGames.put(gameId, game);
    }

    /**
     * Creates a new player and assigns them to the specified game
     * Sends the player's data and the current game state back to the client.
     * Adds this ClientHandler to the game's client handler list.
     *
     * @param name name of the player.
     * @param game the game the player is joining.
     * @throws IOException If an I/O error occurs during communication.
     */
    private void createPlayer(String name, GameState game) throws IOException {
        Player myPlayer = GameInit.createPlayer(name, game);
        clientHandlers.get(gameId).add(this);
        myPlayerId = myPlayer.getPlayerId();
        NetworkUtility.sendPlayer(myPlayer, objectOutputStream);
        NetworkUtility.sendGameState(game, objectOutputStream);
        broadcastMessage(new String[] {Constants.REFRESH_PAGE});
    }

    /**
    * Removes this client handler from the game's player list and resets relevant fields.
    * Called when a player disconnects or leaves the game.
    */
    private void removePlayerFromGame() {
        if (gameId != null && clientHandlers.get(gameId) != null) {
            clientHandlers.get(gameId).remove(this);
        }
        myPlayerId = 0;
        gameId = null;
    }

    /**
    * Handles the logic when a player decides to leave the game lobby (before game starts).
    * Removes the player from the game and checks if a new game master needs to be assigned.
    * If the game has no players left, it removes the game entirely.
    *
    * @param game the game that the player is leaving.
    */
    private void processPlayerLeaveLobby(GameState game) {
        game.removePlayer(game.findMyPlayer(myPlayerId));
        if (!game.setNewGameMaster()) { // if unable to set a new gameMaster, that means there are no more real players left in the game lobby
            availableGames.remove(gameId);
            clientHandlers.remove(gameId);
        }
    }
    
    /**
     * Handles the logic when a player decides to leave the game during gameplay.
     * Sets the player's status to CPU and assigns a new game master if necessary.
     * Removes the game if there are no players left.
     *
     * @param game the game that the player is leaving.
     */
    private void processPlayerLeaveGame(GameState game) {
        game.findMyPlayer(myPlayerId).setCPU(true);
        if (!game.setNewGameMaster()) { // if unable to set a new gameMaster, that means there are no more real players left in the game
            availableGames.remove(gameId);
            clientHandlers.remove(gameId);
        }
    }

    /**
     * Resets the game by clearing all clientHandlers that are mapped to that game
     * Invokes the game reset logic in the GameState
     *
     * @param game the game to reset.
     */
    private void resetGame(GameState game) {
        game.resetGame();
        clientHandlers.get(gameId).clear();
    }
    
    /**
     * Sends the current GameState to all clientHandlers in the same game, which will send them to their respective clients.
     * Used to ensure all players see the most updated state of the game.
     *
     * @param game the current game to broadcast.
     * @throws IOException If an I/O error occurs during communication.
    */
    private void broadcastGameStatus(GameState game) throws IOException {
        for (ClientHandler clientHandler : clientHandlers.get(gameId)) {
            NetworkUtility.sendGameState(game, clientHandler.objectOutputStream);
        }
    }

    /**
     * Sends a specified message to all clients in the same game, excluding the sender.
     *
     * @param message the message to be sent to other players.
     * @throws IOException If an I/O error occurs during communication.
    */
    private void broadcastMessage(String[] message) throws IOException {
        for (ClientHandler clientHandler : clientHandlers.get(gameId)) {
            if (clientHandler.myPlayerId != this.myPlayerId) {
                NetworkUtility.sendMessage(message, clientHandler.objectOutputStream);
            }
        }
    } 

     /**
     * Close all connections between the client and the server
     * 
     * @param socket represents the connection between the server and the client
     * @param objectInputStream represents the pipe for the user to read information to the server
     * @param objectOutputStream represents the pipe for the user to write information to the server
     */
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
