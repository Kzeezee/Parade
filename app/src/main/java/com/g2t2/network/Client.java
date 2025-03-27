package com.g2t2.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.Instant;
import java.util.Random;
import java.util.Scanner;

import com.g2t2.game.GameState;
import com.g2t2.game.StartMenu;
import com.g2t2.types.Player;
import com.g2t2.util.Constants;
import com.g2t2.util.Display;
import com.g2t2.util.Utility;


public class Client implements Runnable {
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    private String username;
    private boolean isPlayingGame = false;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream()); 

            this.username = username;
        } catch (IOException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        } 
    }

    private void joinOrCreateGame() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println(" 📌 To create a game, enter: '" + Constants.CREATE_GAME + "'");
            System.out.println(" 📌 To join a game, enter: '" + Constants.JOIN_GAME + "'");
            System.out.print(Display.colorString(" ➢ Enter your choice: "));

            String userChoice = sc.nextLine();
            if (userChoice.equals(Constants.CREATE_GAME)) {
                createGame();
            } else if (userChoice.equals(Constants.JOIN_GAME)) {
                joinGame();
            } else {
                System.out.println(Display.errorColour(" ❌ Please input either " +
                Constants.CREATE_GAME + " or " + Constants.JOIN_GAME));
                System.out.println();
                continue; // reprompt the user
            }

            waitingArea();
        }
    }

    private String generateGameId() {
        long timestamp = Instant.now().getEpochSecond();
        Random random = new Random();
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder randomPart = new StringBuilder();
        for (int i = 0; i < 4; i++) { // Generate 4 random characters
            randomPart.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return "G" + timestamp + "-" + randomPart; // Format: G<timestamp>-<random>
    }

    public String getUsername() {
        return username;
    }

    private void sendGameState(GameState gameState) {
        try {
            objectOutputStream.writeObject(gameState);
            objectOutputStream.flush();
        } catch (IOException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        } 
    }

    private void sendMessage(String msg) {
        try {
            objectOutputStream.writeObject(new Message(msg));
            objectOutputStream.flush();
        } catch (IOException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        } 
    }

    private void addPlayerToGame(GameState game) {
        game.addPlayer(new Player(username, game.getDeck(), false));
        isPlayingGame = true;
    }

    private void createGame() {
        String generatedGameId = generateGameId();
        GameState game = new GameState(generatedGameId);
        addPlayerToGame(game);
        sendGameState(game);
    }

    private void joinGame() {
        Scanner sc = new Scanner(System.in);
        String input = null;
        while (!isPlayingGame) {
            System.out.println();
            System.out.println(" 📌 To join a game, enter the gameId");
            System.out.print(Display.colorString(" ➢ Enter gameId: "));
            input = sc.nextLine();
            
            // send to ClientHandler to request to join
            sendMessage(input);
        }
    }

    private static void waitingArea() {
        Utility.clearConsoleScreen();
        System.out.println(Constants.WAITING_LOBBY_BANNER);
    }

    private void listenForMessage() {
        while (socket.isConnected()) {
            try {
                Object incomingObject = objectInputStream.readObject();
                if (incomingObject instanceof GameState game) {
                    if (!isPlayingGame) {
                        // add to game
                        addPlayerToGame(game);
                        sendGameState(game);
                    } else if (!game.hasStarted()){
                        // means still in waiting room
                        System.out.println(" 📌 GAME ID: " + game.getGameId());
                        printPlayerList(game);

                    } else {
                        // render game  
                    }
                }
            } catch (IOException e) {
                closeEverything(socket, objectInputStream, objectOutputStream);
            } catch (ClassNotFoundException e) {
                closeEverything(socket, objectInputStream, objectOutputStream);
            }
        }
    }

    private void printPlayerList(GameState game) {
        for (Player player : game.getPlayers()) {
            System.out.println(player.getName());
        }
    }

    private void renderGame() {

    }

    @Override
    public void run() {
        listenForMessage();
    }

    private void closeEverything(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOuputStream) {
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

    public static void main(String[] args) throws IOException {
        // TODO settle exceptions for username input
        // set up client -> connected to server
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your username for the game: ");
        String username = sc.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket, username);
        // send name to clientHandler
        client.sendMessage(username);
        StartMenu.showStartMenu();

        // Starting the Thread
        Thread thread = new Thread(client);
        thread.start();

        client.joinOrCreateGame();
    }
}
