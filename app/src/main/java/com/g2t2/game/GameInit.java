package com.g2t2.game;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.g2t2.util.Constants;
import com.g2t2.util.Display;
import com.g2t2.util.StateFlags;
import com.g2t2.util.Utility;
import com.g2t2.types.*;

/**
 * Class handling all game initialization methods
 * @author Oliver
 */
public class GameInit {

    private static int numOfPlayers;
    private static int option;
    private static int difficulty;

    /**
     * Display the game options in either console or GUI
     * (depending on what isGUIMode in StateFlags is)
     * @author Oliver
     */
    public static void displayGameOptions() {
        if (StateFlags.isGUIMode) {
            // GUI display options
        } else {
            // Console configuration
            Scanner sc = new Scanner(System.in);
            int optionChosen = 0;

            boolean validOptionSelected = false;
            while (!validOptionSelected) {
                System.out.println(Constants.DIVIDER);
                System.out.println(" 📌 Please select an option.");
                System.out.println(" 📌 Play with friends (1)");
                System.out.println(" 📌 Play with CPU (2)");
                System.out.print(Display.colorString(" ⚜ Enter option ➢ "));
                String s = sc.nextLine();
                System.out.print(Constants.ANSI_RESET);
                try {
                    optionChosen = Integer.parseInt(s);
                    switch (optionChosen) {
                        case 1: case 2:
                            // Start options of game
                            validOptionSelected = true;
                            break;
                        default:
                            throw new Exception(Display.errorColour(" ❌ Invalid input"));
                    }
                } catch (NumberFormatException e) {
                    Utility.clearConsoleScreen();
                    System.out.println(Display.errorColour(Constants.GAME_SELECTION_OPTION_ERROR_MSG));
                } catch (Exception e) {
                    Utility.clearConsoleScreen();
                    System.out.println(Display.errorColour(Constants.GAME_SELECTION_OPTION_ERROR_MSG));
                }
            }
            option = optionChosen;
            if (option == 2){
                int choose = 0;

                validOptionSelected = false;
                while (!validOptionSelected) {
                    System.out.println(Constants.DIVIDER);
                    System.out.println(" 📌 Please select a difficulty level (only 1, 2 for now).");
                    System.out.println(" 📌 Easy (1)");
                    System.out.println(" 📌 Medium (2)");
                    System.out.println(" 📌 Difficult (3)");
                    System.out.print(Display.colorString(" ⚜ Enter option ➢ "));
                    String s = sc.nextLine();
                    System.out.print(Constants.ANSI_RESET);
                    try {
                        choose = Integer.parseInt(s);
                        switch (choose) {
                            case 1: case 2:
                                // Start options of game
                                validOptionSelected = true;
                                break;
                            default:
                                throw new Exception(Display.errorColour(" ❌ Invalid input"));
                        }
                    } catch (NumberFormatException e) {
                        Utility.clearConsoleScreen();
                        System.out.println(Display.errorColour(Constants.GAME_SELECTION_OPTION_ERROR_MSG));
                    } catch (Exception e) {
                        Utility.clearConsoleScreen();
                        System.out.println(Display.errorColour(Constants.GAME_SELECTION_OPTION_ERROR_MSG));
                    }
                }
                difficulty = choose;
            }
                displayConsoleGameOptions();
                giveOutTheCards();
                setCPUgameplay();
                GamePlay.startGame();
        }
    }

    /**
     * Method displays the configurable game options in console
     * @author Oliver
     */
    private static void displayConsoleGameOptions() {
        // Allow user to configure players, difficulty?

        /* For local option */ 
        // - ben
        Scanner sc = new Scanner(System.in);
        numOfPlayers = 0;
        // Number of Players
        Utility.clearConsoleScreen();
        System.out.println(Constants.BANNER);
        System.out.println(Constants.DIVIDER);
        while (true) {
            try {
                if (option == 1){
                    System.out.println(" 📌 How many players do you want to play with?");
                } else {
                    System.out.println(" 📌 How many CPU players do you want to play with?");
                }
                System.out.print(Display.colorString(" ➢ Enter the total number of players (2 to 6): "));
                numOfPlayers = sc.nextInt();
                if (numOfPlayers < 2 || numOfPlayers > 6) {
                    throw new IllegalArgumentException(Display.errorColour(" ❌ A minimum of 2 players and a maximum of 6 players are allowed. Please enter a number between 2 to 6."));
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
    }


    /**
     * Method initialises players + parade
     * @author ben
     */
    private static void giveOutTheCards() {
        Utility.clearConsoleScreen();
        System.out.println(Constants.BANNER);
        Scanner sc = new Scanner(System.in);
        ArrayList<Player> players = new ArrayList<>();
        if (option == 1){
            for (int i = 1; i <= numOfPlayers; i++) {
                while (true) {
                    try {
                        System.out.print(Display.colorString(" ➢ Enter the name of player " + i + ": "));
                        String name = sc.nextLine();
                        if (name.equals("")) {
                            throw new InputMismatchException(" 📌 Please enter an appropriate name.");
                        }
                        players.add(new Player(name, ParadeBoard.getDECK()));
                        break;
                    } catch (InputMismatchException e) {
                        System.out.println(e.getMessage());
                        System.out.println();
                    }
                }
            }
        } else {
            // Modified to get player name from player profile
            players.add(new Player(StateManager.getPlayerProfile().getName(), ParadeBoard.getDECK()));
            for (int i = 1; i < numOfPlayers; i++){
                String name = "CPU " + i;
                players.add(new Player(name, ParadeBoard.getDECK()));
            }
        }
        ParadeBoard.setPlayers(players);

        // Setting up the board
        ArrayList<Card> board = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            board.add(ParadeBoard.getDECK().drawCard());
        }
        ParadeBoard.setParade(board);
    }

    public static int setCPUgameplay(){
        return option;
    }

    public static int setDifficulty(){
        return difficulty;
    }
}
