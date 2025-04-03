package com.g2t2.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.g2t2.game.*;
import com.g2t2.game.gameutility.*;
import com.g2t2.network.networkUtility.*;
import com.g2t2.types.Player;
import com.g2t2.util.*;

public class Client implements Runnable {
    private static final int portNumber = 1234;
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private final BlockingQueue<Runnable> mainThreadTasks = new LinkedBlockingQueue<>();
    private boolean isInGame = false;
    private String username;
    private int myPlayerId; // track player

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
    
    private void joinGameLobby(String username) {
        Display.joiningGameLobbyHeader();
        String[] userInput = OnlineLobby.joinGamePrompt(username);
        sendMessage(userInput);
    }

    private void gameWaitingLobby(GameState game) {
        try {
            OnlineLobby.printWaitingRoomDetails(game, myPlayerId);
            boolean isGameMaster = game.getGameMaster().getPlayerId() == myPlayerId;
            String[] userInput = OnlineLobby.activatingUserControlCommands(isGameMaster, game);
            sendMessage(userInput);
        } catch (InterruptedException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        }
    }

    private void renderGame(GameState game) {
        Display.gameHeader();
        Player currentPlayer = game.getCurrentPlayer();
        String[] messageToSendBack = {"", "", Constants.COMPLETE_TURN};

        if (game.isEndOfGame()) {
            GameRenderer.displayScores(game.getPlayers());
            sendMessage(new String[] {Constants.GAME_ENDED}); // signal to clienthandler to reset server side things
            isInGame = false;

            String userChoice = Prompt.promptForNewGame();
            if (userChoice.equals("y")) {
                sendMessage(new String[] {username, Constants.NEW_GAME});
            } else {
                sendMessage(new String[] {Constants.LEAVE_GAME});
            }
            return;
        }

        GameRenderer.displayRoundInformation(game);
        GameRenderer.displayHandCards(NetworkUtility.findMyPlayer(game, myPlayerId));
        if (game.isPut2CollectionRound()) { // put 2 to collection round
            messageToSendBack[2] = Constants.PUT_TO_COLLECTION;
            processPut2CollectionRound(messageToSendBack, currentPlayer, game);
        } else { // normal rounds
            processNormalRound(messageToSendBack, currentPlayer, game);
            }
    }

    private void processPut2CollectionRound(String[] messageToSendBack, Player currentPlayer, GameState game) {
        if (currentPlayer.getCPU()) { // CPU turn
            CPUGameAction.displayCPUTurnAction(currentPlayer);
            if (game.getGameMaster().getPlayerId() == myPlayerId) { // only gameMaster sends back to server
                String[] indexOfChosenCard = CPUGameAction.CPUSelectTwoToCollections(currentPlayer, game);
                messageToSendBack[0] = indexOfChosenCard[0];
                messageToSendBack[1] = indexOfChosenCard[1];
            } else { // the rest don't do anything
                System.out.println("It is " + currentPlayer.getName() + "'s turn. Not Your Turn.");
                return;
            }
        } else if (currentPlayer.getPlayerId() == myPlayerId) { // your turn
                String[] indexOfChosenCard = Prompt.promptToCollectTwo(currentPlayer);
                if (indexOfChosenCard[0].equals(Constants.LEAVE_GAME)) {
                    leaveGame();
                    // bot to take over the turn
                    String[] temp = CPUGameAction.CPUSelectTwoToCollections(currentPlayer, game);
                    indexOfChosenCard[0] = temp[0];
                    indexOfChosenCard[1] = temp[1];
                }
                messageToSendBack[0] = indexOfChosenCard[0];
                messageToSendBack[1] = indexOfChosenCard[1];
        } else {
            System.out.println("It is " + currentPlayer.getName() + "'s turn. Not Your Turn.");
            return;
        }
        sendMessage(messageToSendBack);
    }

    private void processNormalRound(String[] messageToSendBack, Player currentPlayer, GameState game) {
        if (currentPlayer.getCPU()) { // CPU turn
            CPUGameAction.displayCPUTurnAction(currentPlayer);
            if (game.getGameMaster().getPlayerId() == myPlayerId) { // only gameMaster sends back to server
                int indexOfChosenCard = CPUGameAction.chooseCPUCardIdx(currentPlayer, game, -1);
                messageToSendBack[0] = indexOfChosenCard + "";
            } else { // the rest don't do anything
                System.out.println("It is " + currentPlayer.getName() + "'s turn. Not Your Turn.");
                return;
            }
        } else if (currentPlayer.getPlayerId() == myPlayerId) { // your turn
            String indexOfChosenCard = Prompt.promptForCard(currentPlayer, Constants.PROMPT_ADD_TO_PARADE);
            if (indexOfChosenCard.equals(Constants.LEAVE_GAME)) {
                leaveGame();
                // bot to take over the turn
                indexOfChosenCard = CPUGameAction.chooseCPUCardIdx(currentPlayer, game, -1) + "";
            }
            messageToSendBack[0] = indexOfChosenCard; 
        } else {
            System.out.println("It is " + currentPlayer.getName() + "'s turn. Not Your Turn.");
            return;
        }
        sendMessage(messageToSendBack);
    }

    private void sendMessage(String msg[]) {
        try {
            objectOutputStream.writeObject(new Message(msg));
            objectOutputStream.flush();
        } catch (IOException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        } 
    }

    private void leaveGame() {
        isInGame = false;
        sendMessage(new String[] {Constants.LEAVE_GAME});
    }
     
    private void listenForMessage() {
        while (socket.isConnected()) {
            try {
                Object incomingObject = objectInputStream.readObject();
                if (incomingObject instanceof GameState game) {
                    if (!game.getHasStarted()) {
                        mainThreadTasks.add(() -> gameWaitingLobby(game));

                    // this is deal with the problem that the Scanner from activatingUserControlCommands is blocking the main thread
                    } else if (!isInGame && game.getGameMaster().getPlayerId() != myPlayerId) {
                        Utility.clearConsoleScreen();
                        System.out.print(Display.colorString(" ➢ Press ENTER KEY to show game!"));
                        isInGame = true;
                    } else {
                        mainThreadTasks.add(() -> renderGame(game));
                    }

                } else if (incomingObject instanceof Message incomingMessage) {
                    String command = incomingMessage.getMessage()[incomingMessage.getMessage().length-1];
                    if (command.equals(Constants.INVALID_GAME_ID)) { // wrong gameId
                        System.out.println(Constants.INVALID_GAME_ID);
                        NetworkUtility.threadSleep(1000);
                        mainThreadTasks.add(() -> joinGameLobby(username));
                    } else if (command.equals(Constants.FULL_CAPACITY)) { // full capacity
                        System.out.println(Constants.FULL_CAPACITY);
                        NetworkUtility.threadSleep(1000);
                        mainThreadTasks.add(() -> joinGameLobby(username));

                    } else if (command.equals(Constants.REFRESH_PAGE)) {
                        OnlineLobby.setRefreshPage(true);

                    } else if (command.equals(Constants.NEW_GAME)) {
                        //gameJoiningLobby();
                        joinGameLobby(username);

                    }
                } else if (incomingObject instanceof Player player) {
                    myPlayerId = player.getPlayerId();
                }
            } catch (IOException e) {
                closeEverything(socket, objectInputStream, objectOutputStream);
                break;
            } catch (ClassNotFoundException e) {
                closeEverything(socket, objectInputStream, objectOutputStream);
                break;
            } catch (InterruptedException e) {
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
            mainThreadTasks.add(() -> {
                System.out.println("Closing main client...");
                Thread.currentThread().interrupt(); 
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        // TODO settle exceptions for username input
        // set up client -> connected to server
        try {
            Scanner sc = new Scanner(System.in);
            System.out.print("Enter your username for the game: "); 
            String username = sc.nextLine(); // TODO: to take profile name
            Socket socket = new Socket("localhost", portNumber);
            Client client = new Client(socket, username);
            StartMenu.showStartMenu();

             // Starting the Thread
            Thread thread = new Thread(client);
            thread.start();
            client.joinGameLobby(username);

            while (socket.isConnected()) {
                try {
                    Runnable task = client.mainThreadTasks.take(); // Blocks until a task is available
                    task.run();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println(Display.errorColour(" ❌ Server is closed"));
        } 

        System.out.println(Constants.GOODBYE); // TODO: remove after combining with local
    }
}