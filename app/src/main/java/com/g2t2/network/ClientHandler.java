package com.g2t2.network;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
public class ClientHandler implements Runnable { // allows instances to be executed by a separate thread
    
    // keep track of all the clients - to send msg to all of them (broadcast to all)
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
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
        clientHandlers.put(gameId, new ArrayList<>());
        availableGames.put(gameId, game);
        createPlayer(name);
    }

    private void createPlayer(String name) {
        GameState game = findGameState(gameId);
        Player player = new Player(name);
        currentPlayer = player;
        game.addPlayer(player);
        clientHandlers.get(gameId).add(this);
        sendPlayer(player);
        broadcastGameStatus(game); // to put to waiting lobby
    }

    private void createCPU(int numOfCPU, int difficulty) {
        GameState game = findGameState(gameId);
        for (int i = 0; i < numOfCPU; i++ ) {
        Player cpu = new Player(true, difficulty);
        game.addPlayer(cpu);
        }
        broadcastGameStatus(game); // update waiting lobby
    }

    private GameState findGameState(String gameId) {
        for (String id : availableGames.keySet()) {
            if (id.equals(gameId)) {
                return availableGames.get(gameId);
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
                    
                    } else if (messageToProcess.getMessage()[0].equals(Constants.JOIN_GAME)) { // join game
                        // checking if gameId sent in is valid
                        GameState game = findGameState(messageToProcess.getMessage()[1]);
                        if (game == null) {
                            sendMessage(new String[] {Constants.INVALID_GAME_ID});
                        } else if (game.getNumOfPlayers() >= 6) {
                            sendMessage(new String[] {Constants.FULL_CAPACITY});
                        }
                        else {
                            gameId = messageToProcess.getMessage()[1];
                            createPlayer(messageToProcess.getMessage()[2]);
                        }

                    } else if (messageToProcess.getMessage()[0].equals(Constants.ADD_CPU)) { // add cpu
                        createCPU(Integer.parseInt(messageToProcess.getMessage()[1]), Integer.parseInt(messageToProcess.getMessage()[2]));
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
    private void broadcastGameStatus(GameState game) {
        for (ClientHandler clientHandler : clientHandlers.get(gameId)) {
            GameState snapshot = availableGames.get(gameId).deepCopy(); // Create a snapshot
            clientHandler.sendGameState(snapshot);
            // clientHandler.sendGameState(findGameState(gameId));
        }
    }

    private void sendGameState(GameState game) {
        try {
            objectOutputStream.writeObject(game);
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
