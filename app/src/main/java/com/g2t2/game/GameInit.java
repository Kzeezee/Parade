package com.g2t2.game;

import java.util.ArrayList;
import java.util.Scanner;

import com.g2t2.util.StateFlags;
import com.g2t2.util.Utility;
import com.g2t2.types.*;

/**
 * Class handling all game initialization methods
 * @author Oliver
 */
public class GameInit {

    private static int numOfPlayers;

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
            Utility.clearConsoleScreen();
            displayConsoleGameOptions();
            giveOutTheCards();

            // // Test
            // System.out.println("--TEST--");
            // for (Player p : ParadeBoard.getPlayers()) {
            //     System.out.println(p.getName());
            //     for (Card c : p.getCardsOnHand()) {
            //         System.out.println("Colour = " + c.getColour() + "| Value = " + c.getValue());
            //     }
            //     System.out.println();
            // }

            // System.out.println("--PARADE BOARD--");
            // for (Card c : ParadeBoard.getParade()) {
            //     System.out.println("Colour = " + c.getColour() + "| Value = " + c.getValue());
            // }
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
        while (true) {
            try {
                System.out.println("How many players do you want to play with?");
                System.out.print("Enter the number of players (2 to 6): ");
                numOfPlayers = Integer.parseInt(sc.nextLine());
                if (numOfPlayers < 2 || numOfPlayers > 6) {
                    throw new IllegalArgumentException();
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("A minimum of 2 players and a maximum of 6 players are allowed. Please enter a number between 2 to 6.");
                System.out.println();
            }
        }
    }

    /**
     * Method initialises players + parade
     * @author ben
     */
    private static void giveOutTheCards() {
        Scanner sc = new Scanner(System.in);
        ArrayList<Player> players = new ArrayList<>();

        for (int i = 1; i <= numOfPlayers; i++) {
            while (true) {
                try {
                    System.out.print("Enter the name of player " + i + ": ");
                    String name = sc.nextLine();
                    if (name.equals("")) {
                        throw new IllegalArgumentException("Please enter an appropriate name.");
                    }
                    players.add(new Player(name, ParadeBoard.getDECK()));
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                    System.out.println();
                }
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
}
