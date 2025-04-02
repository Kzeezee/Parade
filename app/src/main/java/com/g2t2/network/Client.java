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
    private static final Runnable CLOSE_MAIN_THREAD = () -> {}; // Empty task to signal termination
    private volatile boolean needToRefreshPage = false;
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

    private void gameJoiningLobby() {
        Display.waitingLobbyHeader();
        Scanner sc = new Scanner(System.in);
        boolean hasCorrectInput = false;
        while (!hasCorrectInput) {
            System.out.print(Display.colorString(" ➢ Enter your choice: "));

            String[] userChoice = {username, "", ""};
            userChoice[userChoice.length-1] = sc.nextLine();

            if (userChoice[userChoice.length-1].equals(Constants.CREATE_GAME)) {
                sendMessage(userChoice); // create game
                hasCorrectInput = true;

            } else if (userChoice[userChoice.length-1].equals(Constants.JOIN_GAME)) {
                joinGamePrompt();
                hasCorrectInput = true;

            } else if (userChoice[userChoice.length-1].equals(Constants.GO_BACK)) {
                StartMenu.showStartMenu();
                gameJoiningLobby();
            } else {
                System.out.println(Display.errorColour(" ❌ Please input either a valid command"));
                System.out.println();
                continue; // reprompt the user
            }
        }
    }

    private void joinGamePrompt() {
        Scanner sc = new Scanner(System.in);
        String[] userChoice = {username, "", Constants.JOIN_GAME};

        System.out.println();
        System.out.println(" 📌 To join a game, enter the gameId");
        System.out.print(Display.colorString(" ➢ Enter gameId: "));

        userChoice[1] = sc.nextLine();
        sendMessage(userChoice); // attempt to join game   
    }

    private void printWaitingRoomDetails(GameState game) {
        boolean isGameMaster = game.getGameMaster().getPlayerId() == myPlayerId;
        Utility.clearConsoleScreen();
        System.out.println(Constants.WAITING_LOBBY_BANNER);
        printWaitingRoomCommands(isGameMaster, game);
        System.out.println(Constants.DIVIDER);
        System.out.println(" 📌 GAME ID: " + game.getGameId());
        game.printPlayerList();
        activatingUserControlCommands(isGameMaster, game);
    }

    private void printWaitingRoomCommands(boolean isGameMaster, GameState game) {
        System.out.println(Display.colorString("--- Here are some available commands you can use ---"));
        System.out.println(" 📌 To leave the room, enter '" + Constants.LEAVE_GAME + "'");
        if (isGameMaster) { // show available GameMaster Commands
            System.out.println(Display.colorString("--- GameMaster Specific Commands ---"));
            if (game.getNumOfPlayers() < Constants.MAX_PLAYER_NO) {
                System.out.println(" 📌 To add CPU(s), enter '" + Constants.ADD_CPU + "'");
            } 
            if (game.getNumOfCPU() > 0) {
                System.out.println(" 📌 To remove CPU(s), enter '" + Constants.REMOVE_CPU + "'");
            } 
            System.out.println(" 📌 To start the game, enter '" + Constants.START_GAME + "'");
        }
    }

    private void activatingUserControlCommands(boolean isGameMaster, GameState game) {
        Scanner sc = new Scanner(System.in);
        boolean toContinue = true;
        while (toContinue) {
            toContinue = false;
            System.out.print(Display.colorString(" ➢ Enter command: "));
            String input = sc.nextLine();

            if (needToRefreshPage) { // someone joins
                needToRefreshPage = false;
                System.out.println("📌 There is an update, refreshing page");
                threadSleep(1000);
                sendMessage(new String[] {Constants.ENTER_KEY}); // refresh page

                // commands available to everyone
            } else if (input.equals(Constants.LEAVE_GAME)) { 
                sendMessage(new String[] {Constants.LEAVE_GAME}); 

            } else if (input.equals(Constants.ENTER_KEY)) { // refresh page
                sendMessage(new String[] {Constants.ENTER_KEY});

            } else if (isGameMaster) { // commands available to gameMaster
                if (input.equals(Constants.ADD_CPU)) {
                    if (game.getNumOfPlayers() < Constants.MAX_PLAYER_NO) {
                        String[] CPUInformation = {"", "", Constants.ADD_CPU};
                        String[] userInput = Prompt.promptForCPUDetails(game.getNumOfPlayers());
                        if (userInput[0].equals(Constants.CANCEL_ACTION)) { // user cancelled action
                            sendMessage(new String[] {Constants.ENTER_KEY});
                            break;
                        }
                        System.arraycopy(userInput, 0, CPUInformation, 0, 2);
                        sendMessage(CPUInformation);
                    }
                } else if (game.getNumOfCPU() > 0 && input.equals(Constants.REMOVE_CPU)) {
                    String[] idsToRemove = Prompt.promptForCPURemoval(game);
                    if (idsToRemove[0].equals(Constants.CANCEL_ACTION)) {
                        sendMessage(new String[] {Constants.ENTER_KEY});
                        break;
                    }
                    sendMessage(idsToRemove);
                } else if (input.equals(Constants.START_GAME)) {
                    if (game.getNumOfPlayers() < Constants.MIN_PLAYER_NO) {
                        System.out.println(Display.errorColour(" ❌ Insufficient Number of Players"));
                        toContinue = true;
                    } else  {
                        sendMessage(new String[] {Constants.START_GAME});
                    }
                }
            } else {
                System.out.println(Constants.INVALID_COMMAND);
                toContinue = true;
            }   
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
                        mainThreadTasks.add(() -> printWaitingRoomDetails(game));

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
                        mainThreadTasks.add(() -> joinGamePrompt());
                    } else if (command.equals(Constants.FULL_CAPACITY)) { // full capacity
                        System.out.println(Constants.FULL_CAPACITY);
                        mainThreadTasks.add(() -> joinGamePrompt());

                    } else if (command.equals(Constants.REFRESH_PAGE)) {
                        needToRefreshPage = true;

                    } else if (command.equals(Constants.NEW_GAME)) {
                        gameJoiningLobby();

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
            }
        }
    }

    @Override
    public void run() {
        listenForMessage();
    }

    private void threadSleep(int duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        }
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
            mainThreadTasks.add(CLOSE_MAIN_THREAD);
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
        Socket socket = new Socket("localhost", portNumber);
        Client client = new Client(socket, username);
        StartMenu.showStartMenu();

        // Starting the Thread
        Thread thread = new Thread(client);
        thread.start();
        client.gameJoiningLobby();

        while (socket.isConnected()) {
            try {
                Runnable task = client.mainThreadTasks.take(); // Blocks until a task is available
                task.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}