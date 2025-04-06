package com.g2t2.network.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.g2t2.game.*;
import com.g2t2.game.gameutility.*;
import com.g2t2.network.networkUtility.*;
import com.g2t2.types.Player;
import com.g2t2.util.*;

/**
 * This Client class represents a player in an online game.
 * It communicates with the server and decides what to displays to the player based on player's input.
 * This class is only activated when the users wants to play online (option 2 in the start menu)
 * It also requires the server to be running before an instance of this class can be created
 * 
 * @author Ben
 */
public class Client implements Runnable {
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private final BlockingQueue<Runnable> mainThreadTasks = new LinkedBlockingQueue<>(); // gather things to do for the main thread
    private boolean isInGame = false;
    private boolean hasLeftGame = false;
    private String username;
    private int myPlayerId; // track player in game

    /**
     * Client Constructor.
     * Each client needs to have an object out/inputstream to communicate with the server
     *
     * @param socket represents the connection between the server and the client
     * @param username name of the client (chosen from the player profile)
     */
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

    /**
     * Contains the things that the client will do once a client is created
     * 1. Start listening for message from the server
     * 2. Send the client to the Join Game Lobby
     * 3. Listen for task to do on the main thread
     */
    public void start() {
        // executes the run() method, allowing the client to listen to the server
        Thread thread = new Thread(this);
        thread.start();

        joinGameLobby(username);
        while (!socket.isClosed()) {
            try {
                Runnable task = mainThreadTasks.take(); // blocks until a task is available
                task.run(); // executes the task once available
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }  
    } 

    /**
     * Listens for messages from the server on a separate thread.
     * Handles different object types such as GameState, Message, and Player.
     * Executes corresponding UI updates or logic based on the command or state received.
     * This method ensures smooth interaction between the client and server during the game.
     */
    private void listenForMessage() {
        while (!socket.isClosed()) { // means client is connected to the server
            try {
                Object incomingObject = objectInputStream.readObject();
                if (incomingObject instanceof GameState game) {
                    if (hasLeftGame) { // to deal with the problem when there is no more players playing. If not CPU will keep playing
                        hasLeftGame = false;
                        
                    } else if (!game.getHasStarted()) { // players are joining the game
                        mainThreadTasks.add(() -> gameWaitingLobby(game));

                    // this is used to deal with the problem that the Scanner from activatingUserControlCommands is blocking the main thread,
                    // preventing the rendering of the game
                    } else if (!isInGame && game.getGameMaster().getPlayerId() != myPlayerId) {
                        Utility.clearConsoleScreen();
                        System.out.print(Display.colorString(" ➢ Press ENTER KEY to show game!"));
                        isInGame = true;

                    } else { // the game has started
                        mainThreadTasks.add(() -> renderGame(game));
                    }
                } else if (incomingObject instanceof Message incomingMessage) {
                    // the last index of Message always contains the purpose of the Message
                    String command = incomingMessage.getMessage()[incomingMessage.getMessage().length-1];

                    if (command.equals(Constants.INVALID_GAME_ID)) { // wrong gameId
                        System.out.println(Display.errorColour(" ❌ " + Constants.INVALID_GAME_ID));
                        NetworkUtility.threadSleep(1000); // to show users the error message before the page clears
                        mainThreadTasks.add(() -> joinGameLobby(username));

                    } else if (command.equals(Constants.FULL_CAPACITY)) { // full capacity
                        System.out.println(Display.errorColour(" ❌ " + Constants.FULL_CAPACITY));
                        NetworkUtility.threadSleep(1000); // to show users the error message before the page clears
                        mainThreadTasks.add(() -> joinGameLobby(username));

                    } else if (command.equals(Constants.REFRESH_PAGE)) {
                        OnlineLobby.setRefreshPage(true);

                    } else if (command.equals(Constants.NEW_GAME)) {
                        mainThreadTasks.add(() -> joinGameLobby(username));
                    }

                } else if (incomingObject instanceof Player player) {
                    myPlayerId = player.getPlayerId(); // allows the client to know their player through their playerID
                }
            } catch (IOException e) {
                closeEverything(socket, objectInputStream, objectOutputStream);
            } catch (ClassNotFoundException e) {
                closeEverything(socket, objectInputStream, objectOutputStream);
            }
        }
    }

    /**
     * Places the client in the online game lobby interface.
     * Allows the client to choose between creating a new game or joining an existing one.
     * Sends the user's intent to the server and handles application exit or return to the main menu.
     *
     * @param username name of the client using the lobby.
     */
    private void joinGameLobby(String username) {
        try {
            Display.joiningGameLobbyHeader();
            // get what the user wants to do (join/create)
            String[] userInput = OnlineLobby.joinGamePrompt(username); 
            NetworkUtility.sendMessage(userInput, objectOutputStream);

            // if clients wants to back to start menu or close the application
            if (userInput[0].equals(Constants.GO_BACK) || userInput[0].equals(Constants.CLOSE_APP)) { 
                throw new IOException(userInput[0]);
            }
        } catch (IOException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
            if (e.getMessage().equals(Constants.CLOSE_APP)) {
                Utility.exitGame(); // close the application
            }
        } 
    }

    /**
     * Displays the game waiting room interface to the client.
     * Shows the status of all players in the room and enables the gameMaster
     * to start the game or add CPU players. Other players wait for the game to begin.
     * Sends the chosen user action to the server.
     *
     * @param game current game state representing the game waiting lobby.
     */
    private void gameWaitingLobby(GameState game) {
        try {
            OnlineLobby.printWaitingRoomDetails(game, myPlayerId);
            boolean isGameMaster = game.getGameMaster().getPlayerId() == myPlayerId;
            // get what the users want to do
            String[] userInput = OnlineLobby.activatingUserControlCommands(isGameMaster, game);
            if (userInput[0].equals(Constants.START_GAME)) {
                isInGame = true; // only set for the gameMaster as normal players' will be set later
            }
            NetworkUtility.sendMessage(userInput, objectOutputStream);
        } catch (IOException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        } 
    }

    /**
     * Renders the current state of the game to the client.
     * Displays board layout, player hands, player collections, game round information,
     * and prompts the user to make decisions based on the stage of the game.
     * Handles end-of-game events and prompts players to restart or leave.
     *
     * @param game the current state of the Parade game.
     */
    private void renderGame(GameState game) {
        try {
            Display.gameHeader();
            Player currentPlayer = game.getCurrentPlayer();
            String[] messageToSendBack = {"", "", Constants.COMPLETE_TURN};

            if (game.isEndOfGame()) {
                GameRenderer.displayScores(game.getPlayers());
                isInGame = false;
                // signal to the server to reset things on their side
                NetworkUtility.sendMessage(new String[] {Constants.GAME_ENDED}, objectOutputStream); 
                
                // prompt the player if they want to play again
                if (GamePlayPrompt.promptForNewGame()) {
                    NetworkUtility.sendMessage(new String[] {username, Constants.NEW_GAME}, objectOutputStream);
                } else {
                    NetworkUtility.sendMessage(new String[] {Constants.LEAVE_GAME}, objectOutputStream);
                }
                return;
            }

            GameRenderer.displayRoundInformation(game);
            GameRenderer.displayHandCards(game.findMyPlayer(myPlayerId));
            if (game.isPut2CollectionRound()) { // put 2 to collection round
                messageToSendBack = OnlineGameProcessor.processPut2CollectionRound(currentPlayer, game, myPlayerId, this);
            } else { // normal rounds
                messageToSendBack = OnlineGameProcessor.processNormalRound(currentPlayer, game, myPlayerId, this);
            }
            if (messageToSendBack != null) {
                NetworkUtility.sendMessage(messageToSendBack, objectOutputStream);
            } 
            if (!isInGame ) { // the player has decided to leave the game on his turn
                NetworkUtility.sendMessage(new String[] {Constants.LEAVE_GAME}, objectOutputStream);
                hasLeftGame = true;
            }
        } catch (IOException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        }
    }

    /**
     * Sets the state of whether the client is currently in a game.
     * Used primarily to update internal logic when a player leaves or joins a game.
     *
     * @param isInGame true if the client is currently in a game; false otherwise.
     */
    public void setIsInGame(boolean isInGame) {
        this.isInGame = isInGame;
    }

    /**
     * Starts the client handler's execution by invoking the listener method in a separate thread.
     * This method enables asynchronous communication with the server.
     */
    @Override
    public void run() {
        listenForMessage();
    }

    /**
     * Close all connections between the client and the server
     * 
     * @param socket represents the connection between the server and the client
     * @param objectInputStream represents the pipe for the user to read information to the server
     * @param objectOutputStream represents the pipe for the user to write information to the server
     */
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
            
            // signal to the main thread to stop listening for new task
            mainThreadTasks.add(() -> {
                Thread.currentThread().interrupt(); 
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}