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
            this.username = username;
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream()); 
        } catch (IOException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        } 
    }

    private void joinOrCreateGame() {
        Scanner sc = new Scanner(System.in);
        boolean hasCorrectInput = false;
        while (!hasCorrectInput) {
            System.out.println(" 📌 To create a game, enter: '" + Constants.CREATE_GAME + "'");
            System.out.println(" 📌 To join a game, enter: '" + Constants.JOIN_GAME + "'");
            System.out.print(Display.colorString(" ➢ Enter your choice: "));

            String userChoice = sc.nextLine();
            if (userChoice.equals(Constants.CREATE_GAME)) {
                hasCorrectInput = true;
                createGame();
            } else if (userChoice.equals(Constants.JOIN_GAME)) {
                hasCorrectInput = true;
                joinGame();
            } else {
                System.out.println(Display.errorColour(" ❌ Please input either " +
                Constants.CREATE_GAME + " or " + Constants.JOIN_GAME));
                System.out.println();
            }
        }
    }

    private void createGame() {
        GameState game = new GameState();
        sendGameState(game); // update gameId
        addPlayerToGame(game);
        printWaitingRoomDetails(game);
    }

    private void joinGame() {
        Scanner sc = new Scanner(System.in);
        String input = null;
        System.out.println();
        System.out.println(" 📌 To join a game, enter the gameId");
        System.out.print(Display.colorString(" ➢ Enter gameId: "));
        input = sc.nextLine();
        sendMessage(input); // send to ClientHandler to request to join
    }

    public void addPlayerToGame(GameState game) {
        Player player = new Player(username, game.getDeck(), false);
        game.addPlayer(player);
        isPlayingGame = true;
        sendPlayer(player); // for clientHandler identification
    }

    private void printWaitingRoomDetails(GameState game) {
        Utility.clearConsoleScreen();
        System.out.println(Constants.WAITING_LOBBY_BANNER);
        System.out.println(" 📌 GAME ID: " + game.getGameId());
        game.printPlayerList();
    }

    private void renderGame() {

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

    private void sendPlayer(Player player) {
        try {
            objectOutputStream.writeObject(player);
            objectOutputStream.flush();
        } catch (IOException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        } 
    }

    private void listenForMessage() {
        while (socket.isConnected()) {
            try {
                Object incomingObject = objectInputStream.readObject();
                if (incomingObject instanceof GameState game) {
                    if (!isPlayingGame) { // add to game -> join by gameId
                        addPlayerToGame(game);
                        sendGameState(game); // refresh the player's list
                    } else if (!game.hasStarted()){ // means still in waiting room
                        printWaitingRoomDetails(game);
                    } else {
                        // render game  
                    }
                } else if (incomingObject instanceof Message incomingMessage) {
                    if (incomingMessage.getMessage().equals(Constants.INVALID_GAME_ID)) { // wrong gameId
                        System.out.println(Constants.INVALID_GAME_ID);
                        joinGame();
                    }
                }
            } catch (IOException e) {
                closeEverything(socket, objectInputStream, objectOutputStream);
                break;
            } catch (ClassNotFoundException e) {
                closeEverything(socket, objectInputStream, objectOutputStream);
                break;
            }
        }
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
        StartMenu.showStartMenu();

        // Starting the Thread
        Thread thread = new Thread(client);
        thread.start();
        client.joinOrCreateGame();
    }
}
