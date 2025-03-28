package com.g2t2.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.g2t2.game.GameState;
import com.g2t2.types.Player;
import com.g2t2.util.Constants;

public class ClientHandler implements Runnable { // allows instances to be executed by a separate thread
    
    // keep track of all the clients - to send msg to all of them (broadcast to all)
    public static Map<GameState, List<ClientHandler>> clientHandlers = new HashMap<>();
    public static List<GameState> availableGames = new ArrayList<>();
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private Player currentPlayer; // to know the message is from who
    private String gameId;  // used to check what game the player is playing

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
        clientHandlers.put(game, new ArrayList<>());
        availableGames.add(game);
        createPlayer(name);
    }

    private void createPlayer(String name) {
        GameState game = findGameState(gameId);
        Player player = new Player(name, game.getDeck(), false);
        currentPlayer = player;
        game.addPlayer(player);
        clientHandlers.get(game).add(this);
        sendGameState(game); // to put to waiting area

        System.out.println("Number of clienthandlers: " + clientHandlers.get(game).size());
        System.out.println("Number of players (number): " + game.getNumOfPlayers());
        System.out.println("Number of players: " + game.getPlayers().size());
        System.out.println(name + " Player is created ");
        System.out.println();
    }

    private GameState findGameState(String gameId) {
        for (GameState game : availableGames) {
            if (game.getGameId().equals(gameId)) {
                return game;
            }
        }
        return null;
    }
    
    
    // This method contains everything that is supposed to ran on a separate thread, such as blocking operations
    @Override
    public void run() {
        while (socket.isConnected()) {
            try {
                Object incomingObject = objectInputStream.readObject();
                if (incomingObject instanceof Message messageToProcess) {
                    if (messageToProcess.getMessage()[0].equals(Constants.CREATE_GAME)) { // create game
                        createGame(messageToProcess.getMessage()[2]);
                    
                    } else if (messageToProcess.getMessage()[0].equals(Constants.JOIN_GAME)) {
                        // checking if gameId sent in is valid
                        GameState game = findGameState(messageToProcess.getMessage()[1]);
                        if (game != null) {
                            gameId = messageToProcess.getMessage()[1];
                            createPlayer(messageToProcess.getMessage()[2]);
                        } else {
                            sendMessage(new String[] {Constants.INVALID_GAME_ID});
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Something happened----");
                closeEverything(socket, objectInputStream, objectOutputStream);
                break; // break out from the while loop
            } catch (ClassNotFoundException e) {
                closeEverything(socket, objectInputStream, objectOutputStream);
                break;
            }
        }
    }
    
    // send gameStatus to all players in the game
    private void broadcastGameStatus(GameState gameState) {
        System.out.println("Number of clienthandlers: " + clientHandlers.get(gameState).size());
        System.out.println("Number of players (number): " + gameState.getNumOfPlayers());
        System.out.println("Number of players: " + gameState.getPlayers().size());
        for (ClientHandler clientHandler : clientHandlers.get(gameState)) {
            clientHandler.sendGameState(gameState);
        }
    }

    private void sendGameState(GameState gameState) {
        try {
            objectOutputStream.writeObject(gameState);
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
        clientHandlers.get(findGameState(gameId)).remove(this);
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
