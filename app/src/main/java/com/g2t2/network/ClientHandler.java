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

    // private BufferedReader bufferedReader; // used to read data 
    // private BufferedWriter bufferedWriter; // used to send data to the client 

    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    private String clientName;
    private String gameId;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;

            // need to wrap as OutputStream is a byte stream while streamWriter is a character stream
            // wrapping buffered writer makes it more efficient
            // bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); 
            // bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); 

            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());

            // this.clientName = bufferedReader.readLine(); // when user input 
            // clientHandlers.add(this); // adding to the ArrayList

        } catch (IOException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        }
    }
    
    // This method contains everything that is supposed to ran on a separate thread, such as blocking operations
    @Override
    public void run() {

        while (socket.isConnected()) {
            try {
                Object incomingObject = objectInputStream.readObject();
                if (incomingObject instanceof Client client) {
                    // complete the initialisation after Client has joined a game
                    clientName = client.getUsername();
                    System.out.println(clientName); // testing

                } else if (incomingObject instanceof GameState gameState) {
                    if (!availableGames.contains(gameState)) { // first time. Creating the game.
                        availableGames.add(gameState);
                        gameId = gameState.getGameId();
                    } else {
                        broadcastGameStatus(gameState);
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

    // send gameStatus to all players in the game
    private void broadcastGameStatus(GameState gameState) {
        String currentGameId = gameState.getGameId();

        for (ClientHandler clientHandler : clientHandlers.get(currentGameId)) {
            try {
                objectOutputStream.writeObject(gameState);
                objectOutputStream.flush();
            } catch (IOException e) {
                closeEverything(socket, objectInputStream, objectOutputStream);
            }
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
