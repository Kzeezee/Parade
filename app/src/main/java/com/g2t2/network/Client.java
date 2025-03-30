package com.g2t2.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

import com.g2t2.game.GameState;
import com.g2t2.game.StartMenu;
import com.g2t2.game.gameUtility.GameRenderer;
import com.g2t2.game.gameUtility.Prompt;
import com.g2t2.types.Player;
import com.g2t2.util.Constants;
import com.g2t2.util.Display;
import com.g2t2.util.Utility;

public class Client implements Runnable {
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private String username;
    private Player currentPlayer; // track player

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

    private void gameJoiningLobby() {
        Scanner sc = new Scanner(System.in);
        boolean hasCorrectInput = false;
        while (!hasCorrectInput) {
            System.out.println(" 📌 To create a game, enter: '" + Constants.CREATE_GAME + "'");
            System.out.println(" 📌 To join a game, enter: '" + Constants.JOIN_GAME + "'");
            System.out.print(Display.colorString(" ➢ Enter your choice: "));

            String[] userChoice = {"", "", username};
            userChoice[0] = sc.nextLine();

            if (userChoice[0].equals(Constants.CREATE_GAME)) {
                sendMessage(userChoice); // create game
                hasCorrectInput = true;

            } else if (userChoice[0].equals(Constants.JOIN_GAME)) {
                joinGamePrompt();
                hasCorrectInput = true;

            } else {
                System.out.println(Display.errorColour(" ❌ Please input either " +
                Constants.CREATE_GAME + " or " + Constants.JOIN_GAME));
                System.out.println();
                continue; // reprompt the user
            }
        }
    }

    private void joinGamePrompt() {
        Scanner sc = new Scanner(System.in);
        String[] userChoice = {Constants.JOIN_GAME, "", username};

        System.out.println();
        System.out.println(" 📌 To join a game, enter the gameId");
        System.out.print(Display.colorString(" ➢ Enter gameId: "));

        userChoice[1] = sc.nextLine();
        sendMessage(userChoice); // attempt to join game   
    }

    private void printWaitingRoomDetails(GameState game) {
        boolean isGameMaster = game.getGameMaster().equals(currentPlayer);
        int numberOfPlayersCurrently = game.getNumOfPlayers();
        // Utility.clearConsoleScreen();
        System.out.println(Constants.WAITING_LOBBY_BANNER);
        printWaitingRoomCommands(isGameMaster, numberOfPlayersCurrently);
        System.out.println(Constants.DIVIDER);
        System.out.println(" 📌 GAME ID: " + game.getGameId());
        game.printPlayerList();
        activatingUserControlCommands(isGameMaster, numberOfPlayersCurrently);
    }

    private void printWaitingRoomCommands(boolean isGameMaster, int numberOfPlayersCurrently) {
        System.out.println("> Here are some available commands you can use <");
        System.out.println("> To leave room, enter '" + Constants.LEAVE_GAME + "'");
        if (isGameMaster) { // show available GameMaster Commands
            System.out.println("--GameMaster Specific Commands--");
            if (numberOfPlayersCurrently < 6) {
                System.out.println("> To add CPU, enter '" + Constants.ADD_CPU + "'");
            } 
            System.out.println("> To start game, enter '" + Constants.START_GAME + "'");
        }
            // TODO: enable the removal of CPUs / Players
    }

    private void activatingUserControlCommands(boolean isGameMaster, int numberOfPlayersCurrently) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            String input = sc.nextLine();
            if (isGameMaster) {
                if (numberOfPlayersCurrently < 6 && input.equals(Constants.ADD_CPU)) {
                    String[] CPUInformation = {Constants.ADD_CPU, "", ""};
                    String[] userInput = Prompt.promptForCPUDetails(numberOfPlayersCurrently);
                    System.arraycopy(userInput, 0, CPUInformation, 1, 2);

                    if (CPUInformation == null) { // user input 0 CPU
                        System.out.println("> No CPU added");
                        continue;
                    }
                    sendMessage(CPUInformation);
                    break;
                } else if (input.equals(Constants.START_GAME)) { // haven't implement
                    sendMessage(new String[] {Constants.START_GAME});
                    break;
                }
            }

            if (input.equals(Constants.LEAVE_GAME)) {
                sendMessage(new String[] {Constants.LEAVE_GAME}); // haven't implement
                sc.nextLine();
                break;
            }
            System.out.println(Constants.INVALID_COMMAND);
        }
    }

    private void renderGame(GameState game) { // TODO: show available commands on top
        GameRenderer.displayRoundInformation(game);
        if (game.getCurrentPlayer().getCPU()) { // bots turn
            // TODO

        } else if (game.getCurrentPlayer().equals(currentPlayer)) { // my turn 
            if (game.isPut2CollectionRound()) {
                // TODO
            } else {

                int indexOfChosenCard = Prompt.promptForCard(Constants.PROMPT_ADD_TO_PARADE, currentPlayer);
                String[] messageToSendBack = {Constants.COMPLETE_TURN, indexOfChosenCard + ""};
                sendMessage(messageToSendBack);
            }
            
        } else { // not the player turn
            System.out.println("Not Your Turn.");
        }
    }

    private void sendMessage(String msg[]) {
        try {
            objectOutputStream.writeObject(new Message(msg));
            objectOutputStream.flush();
        } catch (IOException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        } 
    }

    private void listenForMessage() {
        while (socket.isConnected()) {
            try {
                Object incomingObject = objectInputStream.readObject();
                System.out.println("Received object: " + incomingObject);
                if (incomingObject instanceof GameState game) {
                    System.out.println("number of player: " + game.getNumOfPlayers());
                    System.out.println("GameState received: " + game.getGameId());
                    if (!game.getHasStarted()){ // means still in waiting room
                        printWaitingRoomDetails(game);
                    } else { // game started
                        renderGame(game);
                    }

                } else if (incomingObject instanceof Message incomingMessage) {
                    if (incomingMessage.getMessage()[0].equals(Constants.INVALID_GAME_ID)) { // wrong gameId
                        System.out.println(Constants.INVALID_GAME_ID);
                        joinGamePrompt();
                    } else if (incomingMessage.getMessage()[0].equals(Constants.FULL_CAPACITY)) { // full capacity
                        System.out.println(Constants.FULL_CAPACITY);
                        joinGamePrompt();
                    }

                } else if (incomingObject instanceof Player player) {
                    currentPlayer = player;
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
        client.gameJoiningLobby();
    }
}
