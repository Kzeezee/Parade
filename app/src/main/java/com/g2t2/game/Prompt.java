package com.g2t2.game;

import java.util.InputMismatchException;
import java.util.Scanner;

import com.g2t2.types.Card;
import com.g2t2.types.Player;
import com.g2t2.util.Constants;
import com.g2t2.util.Display;

public class Prompt {
    public static final String GAME_DIFFICULTY_OPTIONS = " 📌 Choose the difficulty level for your CPU players\n" + //
                                                                " 📌 Easy (1)\n" + //
                                                                " 📌 Medium (2)\n" + //
                                                                " 📌 Difficult (3)\n" + //
                                                                Display.colorString(" ⚜ Enter option ➢ ");

    public static final String LOCAL_MODE_OPTIONS = " 📌 Please select an option.\n" 
                                                    + " 📌 Play with friends (1)\n"
                                                    + " 📌 Play with CPU (2)\n" 
                                                    + Display.colorString(" ⚜ Enter option ➢ ");

    public static final String PLAYER_NO_ERROR_MESSAGE = Display.errorColour(" ❌ A minimum of 2 players and a maximum of 6 players are allowed." 
    + "Please enter a number between 2 to 6.");

    // ask whether the player wants to play another round
    public static void promptForNewGame() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                System.out.println(" 📌 The game has ended. 📌 ");
                System.out.println(Display.colorString(" ⚜ Do you want to play another game? (y/n) ➢ "));
                String confirmation = sc.nextLine();

                if (confirmation.equalsIgnoreCase("y")) {
                    StartMenu.showStartMenu();
                    GamePlay.startGame();
                } else if (confirmation.equalsIgnoreCase("n")) {
                    Display.sayGoodBye();
                    System.exit(0);
                }
                System.out.println(Constants.ANSI_RESET);
            } catch (IllegalArgumentException e) {
                System.out.println();
            }
        }
    }

}
