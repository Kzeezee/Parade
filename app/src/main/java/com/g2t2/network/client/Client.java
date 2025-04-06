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
     * @param socket Represents the connection between the server and the client
     * @param username Name of the client (chosen from the player profile)
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
     * This function is running a separate thread.
     * This allows the client to listen for information from the server and take appropriate actions
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
     * Puts the client in a lobby where they can specify if they want to
     * join an existing game or create a new game
     * 
     * @param username Name of the client
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
     * Puts the client in the game waiting lobby where they can see the status of the participants in the game
     * GameMaster can also add CPUs during this stage
     *
     * @param game Represents the current Parade game
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
     * This renders the current GameState to the client.
     * More specifically, it shows the Parade board, the Collection of each player,
     * the cards on the their hand, who's turn it is and the current stage of the game
     * 
     * @param game Represents the current Parade Game
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
     * Used when a player leaves the game during his turn.
     * 
     * @param isInGame true represents that the client is currently in a game
     */
    public void setIsInGame(boolean isInGame) {
        this.isInGame = isInGame;
    }

    /**
     * To execute the listening of message from the server on another thread
     */
    @Override
    public void run() {
        listenForMessage();
    }

    /**
     * Close all connections between the client and the server
     * 
     * @param socket Represents the connection between the server and the client
     * @param objectInputStream Represents the pipe for the user to read information to the server
     * @param objectOutputStream Represents the pipe for the user to write information to the server
     */
    private void closeEverything(Socket socket, ObjectInputStream objectInputStream, ObjectOutputStream objectOuputStream) {
        try {
            if (objectInputStream != null) {
                objectInputStream.close();
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