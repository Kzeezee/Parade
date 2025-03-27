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

public class ClientHandler implements Runnable { // allows instances to be executed by a separate thread
    
    // keep track of all the clients - to send msg to all of them (broadcast to all)
    public static Map<String, List<ClientHandler>> clientHandlers = new HashMap<>();
    public static List<GameState> availableGames = new ArrayList<>();
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String clientName;
    private String gameId;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());

            Object incomingObject = objectInputStream.readObject();
            if (incomingObject instanceof Message msg) {
                clientName = msg.getMessage();
            }

        } catch (IOException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        } catch (ClassNotFoundException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        }
    }
    
    // This method contains everything that is supposed to ran on a separate thread, such as blocking operations
    @Override
    public void run() {
        while (socket.isConnected()) {
            try {
                Object incomingObject = objectInputStream.readObject();
                if (incomingObject instanceof GameState gameState) {
                    if (!availableGames.contains(gameState)) { // first time. Creating the game.
                        availableGames.add(gameState);
                        gameId = gameState.getGameId();
                        System.out.println(gameId); // testing

                    } else { // game started
                        broadcastGameStatus(gameState);
                    }
    
                } else if (incomingObject instanceof Message messageToProcess) {
                    // finding the correct game
                    for (GameState game : availableGames) {
                        if (checkGameId(game, messageToProcess.getMessage())) {
                            sendGameState(game);
                        }
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

    private boolean checkGameId(GameState gameState, String gameId) {
        if (gameState.getGameId().equals(gameId)) {
            return true;
        }
        return false;
    }

    private void sendGameState(GameState gameState) {
        try {
            objectOutputStream.writeObject(gameState);
            objectOutputStream.flush();
        } catch (IOException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        }
    }

    // send gameStatus to all players in the game
    private void broadcastGameStatus(GameState gameState) {
        String currentGameId = gameState.getGameId();
        for (ClientHandler clientHandler : clientHandlers.get(currentGameId)) {
            sendGameState(gameState);
        }
    }

    private void removeClientHandler() {
        clientHandlers.get(gameId).remove(clientName);
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
