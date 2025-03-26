package com.g2t2.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.Random;
import java.time.Instant;

import com.g2t2.game.StartMenu;
import com.g2t2.types.CardDeck;
import com.g2t2.types.ParadeBoard;
import com.g2t2.types.Player;
import com.g2t2.util.*;


public class Client implements Runnable{
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); 
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
            this.username = username;
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        } 
    }

    private void clientHandlerInitialisation() {
        try {
            // this is used to send over the username to the ClientHandler during initalisation phase
            bufferedWriter.write(username);
            bufferedWriter.newLine(); // to add a next line
            bufferedWriter.flush(); // to send to the server    
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    private void sendMessage() {
        try {
            Scanner sc = new Scanner(System.in);
            while (socket.isConnected()) { // loop so client can continuing sending message
                String messageToSend = sc.nextLine();
                bufferedWriter.write(username + ": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
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
                break;
            } else if (userChoice.equals(Constants.JOIN_GAME)) {
                joinGame();
                break;
            } else {
                System.out.println(Display.errorColour("❌ Please input either " +
                Constants.CREATE_GAME + " or " + Constants.JOIN_GAME));
                System.out.println();
            }
        }
    }

    private String generateGameId() {
        Random random = new Random();
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        long timestamp = Instant.now().getEpochSecond();
        StringBuilder randomPart = new StringBuilder();

        for (int i = 0; i < 4; i++) { // Generate 4 random characters
            randomPart.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return "G" + timestamp + "-" + randomPart; // Format: G<timestamp>-<random>
    }

    private void createGame() {
        // NOT COMPLETED
        String gameId = generateGameId();
        new Player(username, ParadeBoard.getDECK(), true); // create gameMaster
        System.out.println(gameId);
    }

    private void joinGame() {
        // NOT COMPLETED
        System.out.println("testing");
    }

    private void listenForMessage() {
        String msgFromGroupChat;
        while (socket.isConnected()) {
            try {
                msgFromGroupChat = bufferedReader.readLine();
                System.out.println(msgFromGroupChat);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    @Override
    public void run() {
        listenForMessage();
    }

    private void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
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
        client.clientHandlerInitialisation();

        // let client decide whether to join game / create game
        StartMenu.showStartMenu();
        client.joinOrCreateGame();

        // Starting the Thread
        Thread thread = new Thread(client);
        thread.start();

        client.sendMessage();
    }
}
