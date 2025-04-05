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
    private final BlockingQueue<Runnable> mainThreadTasks = new LinkedBlockingQueue<>();
    private boolean isInGame = false;
    private boolean hasLeftGame = false;
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
        try {
            Display.joiningGameLobbyHeader();
            String[] userInput = OnlineLobby.joinGamePrompt(username);
            NetworkUtility.sendMessage(userInput, objectOutputStream);
            if (userInput[0].equals(Constants.GO_BACK) || userInput[0].equals(Constants.CLOSE_APP)) { 
                throw new IOException(userInput[0]);
            }
        } catch (IOException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
            if (e.getMessage().equals(Constants.CLOSE_APP)) {
                Utility.exitGame();
            }
        } 
    }

    private void gameWaitingLobby(GameState game) {
        try {
            OnlineLobby.printWaitingRoomDetails(game, myPlayerId);
            boolean isGameMaster = game.getGameMaster().getPlayerId() == myPlayerId;
            String[] userInput = OnlineLobby.activatingUserControlCommands(isGameMaster, game);
            if (userInput[0].equals(Constants.START_GAME)) {
                isInGame = true;
            }
            NetworkUtility.sendMessage(userInput, objectOutputStream);
        } catch (IOException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        } 
    }

    private void renderGame(GameState game) {
        try {
            Display.gameHeader();
            Player currentPlayer = game.getCurrentPlayer();
            String[] messageToSendBack = {"", "", Constants.COMPLETE_TURN};

            if (game.isEndOfGame()) {
                GameRenderer.displayScores(game.getPlayers());
                NetworkUtility.sendMessage(new String[] {Constants.GAME_ENDED}, objectOutputStream); // signal to clienthandler to reset server side things
                isInGame = false;

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
            if (!isInGame ) { // player has left
                NetworkUtility.sendMessage(new String[] {Constants.LEAVE_GAME}, objectOutputStream);
                hasLeftGame = true;
            }
        } catch (IOException e) {
            closeEverything(socket, objectInputStream, objectOutputStream);
        }
    }

    public void setIsInGame(boolean isInGame) {
        this.isInGame = isInGame;
    }
     
    private void listenForMessage() {
        while (!socket.isClosed()) {
            try {
                Object incomingObject = objectInputStream.readObject();
                if (incomingObject instanceof GameState game) {
                    if (hasLeftGame) { // to deal with the problem if not CPU will keep playing
                        hasLeftGame = false;
                    } else if (!game.getHasStarted()) {
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
                        mainThreadTasks.add(() -> joinGameLobby(username));

                    }
                } else if (incomingObject instanceof Player player) {
                    myPlayerId = player.getPlayerId();
                }
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
                objectInputStream.close();
            }
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
            if (socket != null) {
                socket.close();
            }
            mainThreadTasks.add(() -> {
                Thread.currentThread().interrupt(); 
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        // Starting the Thread
        Thread thread = new Thread(this);
        thread.start();
        joinGameLobby(username);

        while (socket.isConnected()) {
            try {
                Runnable task = mainThreadTasks.take(); // Blocks until a task is available
                task.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }  
    } 
}