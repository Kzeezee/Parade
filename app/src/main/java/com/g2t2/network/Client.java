package com.g2t2.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

import org.checkerframework.checker.units.qual.C;

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

            waitingArea();
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
        int numberOfPlayers = game.getNumOfPlayers();
        Utility.clearConsoleScreen();
        System.out.println(Constants.WAITING_LOBBY_BANNER);
        printWaitingRoomCommands(isGameMaster, numberOfPlayers);
        System.out.println(Constants.DIVIDER);
        System.out.println(" 📌 GAME ID: " + game.getGameId());
        game.printPlayerList();
        activatingUserControlCommands(isGameMaster, numberOfPlayers);
    }

    private void printWaitingRoomCommands(boolean isGameMaster, int numberOfPlayers) {
        System.out.println("> Here are some available commands you can use <");
        System.out.println("> To leave room, enter '" + Constants.LEAVE_GAME + "'");
        if (isGameMaster) { // show available GameMaster Commands
            System.out.println("--GameMaster Specific Commands--");
            if (numberOfPlayers < 6) {
                System.out.println("> To add CPU, enter '" + Constants.ADD_CPU + "'");
            } 
            System.out.println("> To start game, enter '" + Constants.START_GAME + "'");
        }
            // TODO: enable the removal of CPUs / Players
    }

    private void activatingUserControlCommands(boolean isGameMaster, int numberOfPlayers) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            String input = sc.nextLine();
            if (isGameMaster) {
                if (numberOfPlayers < 6 && input.equals(Constants.ADD_CPU)) {
                    String[] CPUInformation = addCPU(numberOfPlayers);
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
                break;
            }
            System.out.println(Constants.INVALID_COMMAND);
        }
    }

    private String[] addCPU(int numOfPlayers) {
        Scanner sc = new Scanner(System.in);
        int maxNumOfCPU = 6 -numOfPlayers;
        int numOfCPU = 0;
        while (true) {
            try {        
                System.out.println(" 📌 How many CPU do you want to add?"); 
                System.out.print(Display.colorString(" ➢ Enter a number (max = " + maxNumOfCPU + "): "));
                numOfCPU = sc.nextInt();
                if (numOfCPU < 0 || numOfCPU > maxNumOfCPU) {
                    throw new IllegalArgumentException(Display.errorColour(" ❌ Please enter a number between 0 to " 
                    + maxNumOfCPU + ". Maximum number of players = 6"));
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println(Display.errorColour(" ❌ Please enter a number"));
                System.out.println();
                sc.nextLine();
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                System.out.println();
            }
        }

        if (numOfCPU == 0) {
            return null;
        }
        
        int difficulty = 0;
        while (true) {
            try {
                System.out.print(Constants.ANSI_RESET);
                System.out.println(Constants.DIVIDER);
                System.out.println("Choose the difficulty level for your CPU players");
                System.out.println(" Easy (1)");
                System.out.println(" Medium (2)");
                System.out.println(" Hard (3)");
                System.out.print(Display.colorString(" ⚜ Enter option ➢ "));
                difficulty = sc.nextInt();
                System.out.print(Constants.ANSI_RESET);
                if (difficulty < 1 || difficulty > 3) {
                    throw new IllegalArgumentException(Display.errorColour(" ❌ Please enter a number between 1 to 3"));
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println(Display.errorColour(" ❌ Please enter a number"));
                System.out.println();
                sc.nextLine();
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                System.out.println();
            }
        }
        return new String[] {Constants.ADD_CPU, "" + numOfCPU, "" + difficulty};
    }

    private void activatingUserControlCommands(boolean isGameMaster, int numberOfPlayers) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            String input = sc.nextLine();
            if (isGameMaster) {
                if (numberOfPlayers < 6 && input.equals(Constants.ADD_CPU)) {
                    String[] CPUInformation = addCPU(numberOfPlayers);
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
                break;
            }
            System.out.println(Constants.INVALID_COMMAND);
        }
    }

    private String[] addCPU(int numOfPlayers) {
        Scanner sc = new Scanner(System.in);
        int maxNumOfCPU = 6 -numOfPlayers;
        int numOfCPU = 0;
        while (true) {
            try {        
                System.out.println(" 📌 How many CPU do you want to add?"); 
                System.out.print(Display.colorString(" ➢ Enter a number (max = " + maxNumOfCPU + "): "));
                numOfCPU = sc.nextInt();
                if (numOfCPU < 0 || numOfCPU > maxNumOfCPU) {
                    throw new IllegalArgumentException(Display.errorColour(" ❌ Please enter a number between 0 to " 
                    + maxNumOfCPU + ". Maximum number of players = 6"));
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println(Display.errorColour(" ❌ Please enter a number"));
                System.out.println();
                sc.nextLine();
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                System.out.println();
            }
        }

        if (numOfCPU == 0) {
            return null;
        }
        
        int difficulty = 0;
        while (true) {
            try {
                System.out.print(Constants.ANSI_RESET);
                System.out.println(Constants.DIVIDER);
                System.out.println("Choose the difficulty level for your CPU players");
                System.out.println(" Easy (1)");
                System.out.println(" Medium (2)");
                System.out.println(" Hard (3)");
                System.out.print(Display.colorString(" ⚜ Enter option ➢ "));
                difficulty = sc.nextInt();
                System.out.print(Constants.ANSI_RESET);
                if (difficulty < 1 || difficulty > 3) {
                    throw new IllegalArgumentException(Display.errorColour(" ❌ Please enter a number between 1 to 3"));
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println(Display.errorColour(" ❌ Please enter a number"));
                System.out.println();
                sc.nextLine();
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
                System.out.println();
            }
        }
        return new String[] {Constants.ADD_CPU, "" + numOfCPU, "" + difficulty};
    }

    private void renderGame() {

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
                if (incomingObject instanceof GameState game) {
                    System.out.println("number of player: " + game.getNumOfPlayers());
                    if (!game.hasStarted()){ // means still in waiting room
                        printWaitingRoomDetails(game);
                    } else {
                        // render game  
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
        client.gameJoiningLobby();
    }
}
