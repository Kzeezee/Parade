package com.g2t2.network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.g2t2.game.GameState;
import com.g2t2.game.StartMenu;
import com.g2t2.game.gameutility.CPUGameAction;
import com.g2t2.game.gameutility.GameRenderer;
import com.g2t2.game.gameutility.Prompt;
import com.g2t2.types.Player;
import com.g2t2.util.Constants;
import com.g2t2.util.Display;
import com.g2t2.util.Utility;

public class Client implements Runnable {
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
        boolean isGameMaster = game.getGameMaster().getPlayerId() == myPlayerId;
        int numberOfPlayersCurrently = game.getNumOfPlayers();
        Utility.clearConsoleScreen();
        System.out.println(Constants.WAITING_LOBBY_BANNER);
        printWaitingRoomCommands(isGameMaster, numberOfPlayersCurrently);
        System.out.println(Constants.DIVIDER);
        System.out.println(" 📌 GAME ID: " + game.getGameId());
        game.printPlayerList();
        activatingUserControlCommands(isGameMaster, numberOfPlayersCurrently);
    }

    private void printWaitingRoomCommands(boolean isGameMaster, int numberOfPlayersCurrently) {
        System.out.println("> Here are some available commands you can use <");
        System.out.println("> To refresh the page, press the ENTER KEY");
        // TODO: Implement LEAVE GAME
        // System.out.println("> To leave the room, enter '" + Constants.LEAVE_GAME + "'");
        if (isGameMaster) { // show available GameMaster Commands
            System.out.println("--GameMaster Specific Commands--");
            if (numberOfPlayersCurrently < 6) {
                System.out.println("> To add CPU(s), enter '" + Constants.ADD_CPU + "'");
            } 
            System.out.println("> To start the game, enter '" + Constants.START_GAME + "'");
        }
            // TODO: enable the removal of CPUs / Players
    }

    // CHANGE ALL TO RENDER PAGE FIRST
    private void activatingUserControlCommands(boolean isGameMaster, int numberOfPlayersCurrently) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.print(Display.colorString(" ➢ Enter command: "));
            String input = sc.nextLine();

            if (needToRefreshPage) { // someone joins
                needToRefreshPage = false;
                sendMessage(new String[] {Constants.ENTER_KEY});
                System.out.println("📌 There is an update, refreshing page");
                threadSleep(1000);
                break;
            }

            if (isGameMaster) { // commands available to gameMaster
                if (input.equals(Constants.ADD_CPU)) {
                    if (numberOfPlayersCurrently < 6) {
                        String[] CPUInformation = {Constants.ADD_CPU, "", ""};
                        String[] userInput = Prompt.promptForCPUDetails(numberOfPlayersCurrently);
                        System.arraycopy(userInput, 0, CPUInformation, 1, 2);

                        if (CPUInformation == null) { // user input 0 CPU
                            System.out.println("> No CPU added");
                            continue;
                        }
                        sendMessage(CPUInformation);
                        break;
                    }
                    
                } else if (input.equals(Constants.START_GAME)) {
                    if (numberOfPlayersCurrently < 2) {
                        System.out.println(Display.errorColour("❌ Insufficient Number of Players"));
                        continue;
                    }
                    sendMessage(new String[] {Constants.START_GAME});
                    break;
                }
            }
            // commands available to everyone
            if (input.equals(Constants.ENTER_KEY)) { // refresh page
                sendMessage(new String[] {Constants.ENTER_KEY});
                break;
                // TODO: Implement LEAVE GAME
            // } else if (input.equals(Constants.LEAVE_GAME)) {
            //     System.out.println("LEFT GAME");
            //     sendMessage(new String[] {Constants.LEAVE_GAME}); 
            //     break;
            } 
            System.out.println(Constants.INVALID_COMMAND);
        }
    }

    private void renderGame(GameState game) { // TODO: show available commands on top (only can use during your turn)
        Display.printGameHeader();
        Player currentPlayer = game.getCurrentPlayer();
        String[] messageToSendBack = {Constants.COMPLETE_TURN, "", ""};
        // System.out.println("Number of players: " + game.getNumOfPlayers());
        // System.out.println("Number of cards left: " + game.getDeck().getCardsInDeck().size());
        // System.out.println("Player Turn: " + game.getCurrentPlayer().getName());

        if (game.isEndOfGame()) {
            GameRenderer.displayScores(game.getPlayers());
            return;

            // TODO: prompt user if they want to play again
        }

        GameRenderer.displayRoundInformation(game);
        GameRenderer.displayHandCards(findMyPlayer(game));
        if (game.isPut2CollectionRound()) { // put 2 to collection round
            messageToSendBack[0] = Constants.PUT_TO_COLLECTION;
            if (currentPlayer.getCPU()) { // CPU turn
                CPUGameAction.displayCPUTurnAction(currentPlayer);
                if (game.getGameMaster().getPlayerId() == myPlayerId) { // only gameMaster sends back to server
                    int[] indexOfChosenCard = CPUGameAction.CPUSelectTwoToCollections(currentPlayer, game);
                    messageToSendBack[1] = indexOfChosenCard[0] + "";
                    messageToSendBack[2] = indexOfChosenCard[1] + "";
                } else { // the rest don't do anything
                    return; 
                }
            } else if (currentPlayer.getPlayerId() == myPlayerId) { // your turn
                    int[] indexOfChosenCard = Prompt.promptToCollectTwo(currentPlayer);
                    messageToSendBack[1] = indexOfChosenCard[0] + "";
                    messageToSendBack[2] = indexOfChosenCard[1] + ""; 
            } else { // not your turn
                System.out.println("It is " + currentPlayer.getName() + "'s turn. Not Your Turn.");
                return;
            }

        } else { // normal rounds
            if (currentPlayer.getCPU()) { // CPU turn
                CPUGameAction.displayCPUTurnAction(currentPlayer);
                if (game.getGameMaster().getPlayerId() == myPlayerId) { // only gameMaster sends back to server
                    int indexOfChosenCard = CPUGameAction.chooseCPUCardIdx(currentPlayer, game, -1);
                    messageToSendBack[1] = indexOfChosenCard + "";
                } else { // the rest don't do anything
                    return;
                }
            } else if (currentPlayer.getPlayerId() == myPlayerId) { // your turn
                int indexOfChosenCard = Prompt.promptForCard(Constants.PROMPT_ADD_TO_PARADE, currentPlayer);
                messageToSendBack[1] = indexOfChosenCard + ""; 
            } else { // not your turn
                System.out.println("It is " + currentPlayer.getName() + "'s turn. Not Your Turn.");
                return;
            }
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

    private Player findMyPlayer(GameState game) {
        for (Player player : game.getPlayers()) {
            if (player.getPlayerId() == myPlayerId) {
                return player;
            }
        }
        return null;
    }
     
    private void listenForMessage() {
        while (socket.isConnected()) {
            try {
                // System.out.println("needToRefreshGame: " + needToRefreshPage);
                Object incomingObject = objectInputStream.readObject();
                // System.out.println("Received object: " + incomingObject);
                if (incomingObject instanceof GameState game) {
                    // System.out.println("Number of player: " + game.getNumOfPlayers());
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
                    String command = incomingMessage.getMessage()[0];
                    if (command.equals(Constants.INVALID_GAME_ID)) { // wrong gameId
                        System.out.println(Constants.INVALID_GAME_ID);
                        mainThreadTasks.add(() -> joinGamePrompt());
                    } else if (command.equals(Constants.FULL_CAPACITY)) { // full capacity
                        System.out.println(Constants.FULL_CAPACITY);
                        mainThreadTasks.add(() -> joinGamePrompt());

                    } else if (command.equals(Constants.REFRESH_PAGE)) {
                        needToRefreshPage = true;
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
        Socket socket = new Socket("localhost", 1234);
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


// private InputStream originalIn; // To restore original System.in later
// private PipedInputStream simulatedInput;
// private PipedOutputStream simulatedOutput;

//  // artificially insert the ENTER_KEY into client's system.in
//  private void simulateEnterKey() {
//     try {
//         simulatedOutput.write((Constants.ENTER_KEY + "\n").getBytes());
//         simulatedOutput.flush();
//     } catch (IOException e) {
//         e.printStackTrace();
//     }
// }

// private void overrideSystemIn() throws IOException {
//     originalIn = System.in; // Save original System.in
//     simulatedInput = new PipedInputStream();
//     simulatedOutput = new PipedOutputStream(simulatedInput);
//     System.setIn(simulatedInput); // Override System.in
// }

// private void restoreSystemIn() throws IOException {
//     System.setIn(originalIn); // Restore original System.in
//     simulatedIn.close();
//     simulatedOut.close();
// }
