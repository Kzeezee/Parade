package com.g2t2.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.time.Instant;
import java.util.Random;
import java.util.Scanner;

import com.g2t2.game.GameState;
import com.g2t2.game.StartMenu;
import com.g2t2.types.ParadeBoard;
import com.g2t2.types.Player;
import com.g2t2.util.Constants;
import com.g2t2.util.Display;


public class Client implements Runnable, Serializable {
    private transient Socket socket;

    // private BufferedReader bufferedReader;
    // private BufferedWriter bufferedWriter;

    private transient ObjectOutputStream objectOutputStream;
    private transient ObjectInputStream objectInputStream;

    private String username;
    private String gameId;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;

            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());

            // MySerializableClass obj = new MySerializableClass();
            // objectOutputStream.writeObject(obj);
            // objectOutputStream.flush();

            // bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); 
            // bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); 

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

            //shift to waiting area
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

    public void sendClient() {
        try {
            objectOutputStream.writeObject(this);
            objectOutputStream.flush();
        } catch (IOException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        } 
    }

    public String getUsername() {
        return username;
    }

    public String getGameId() {
        return gameId;
    }

    private void createGame() {
        // NOT COMPLETED
        String generatedGameId = generateGameId();
        GameState game = new GameState(generatedGameId);
        new Player(username, ParadeBoard.getDECK(), false);
        this.gameId = generatedGameId;
        sendClient(); // to update ClientHandler information

    }

    private void joinGame() {
        // NOT COMPLETED
        System.out.println("Success");
        // Scanner sc = new Scanner(System.in);
        // String input = null;
        // while () {
        //     System.out.println(" 📌 To join a game, enter the gameId");
        //     System.out.print(Display.colorString(" ➢ Enter gameId: "));
        //     input = sc.nextLine();
        // }

    }

    private void listenForMessage() {
        
        while (socket.isConnected()) {
            try {
                Object incomingObject = objectInputStream.readObject();
            } catch (IOException e) {
                closeEverything(socket, objectInputStream, objectOutputStream);
            } catch (ClassNotFoundException e) {
                closeEverything(socket, objectInputStream, objectOutputStream);
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
