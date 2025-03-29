package com.g2t2.game.gameUtility;

import java.util.InputMismatchException;
import java.util.Scanner;

import com.g2t2.game.GamePlay;
import com.g2t2.game.StartMenu;
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

    // return String[] = {"number of cpu to add", "difficulty level"} -> string cause easier for me to process in network
    public static String[] promptForCPUDetails(int numOfPlayers) {
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
                System.out.println(Prompt.GAME_DIFFICULTY_OPTIONS);
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
        return new String[] {"" + numOfCPU, "" + difficulty};
    }

    // return the index of the card
    public static int promptForCard(Player player) {
        int chosenCardIndex = 0;
        Scanner sc = new Scanner(System.in);
        int numOfCards = player.getCardsOnHand().size();
        while (true) {
            try {
                System.out.println(Constants.ANSI_RESET + " 📌 Which card do you want to place in the Parade?");
                System.out.print(
                        Display.colorString(" ⚜ Choose card number (1 to " + numOfCards + ") or 'q' to quit ➢ "));
                String chosenCardString = sc.nextLine();
                if (chosenCardString.equalsIgnoreCase("q")) {
                    Display.sayGoodBye();
                    System.exit(0);
                }
                chosenCardIndex = Integer.parseInt(chosenCardString);

                // Confirmation on card choice
                System.out.println(Constants.ANSI_RESET + " 📌 Chosen Card Number: " + (chosenCardIndex));
                System.out.print(Display.colorString(" ⚜ Enter 'y' to confirm or 'q' to quit ➢ "));

                String confirmation = sc.nextLine();
                if (confirmation.length() > 1 || !Character.isLetter(confirmation.charAt(0))
                        || Character.toLowerCase(confirmation.charAt(0)) != 'y') {
                    System.out.println();
                    continue;

                }
                break;
            } catch (InputMismatchException e) {
                System.out.println(Display.errorColour(" ❌ Please input a number"));
                System.out.println();
                sc.nextLine();
            } catch (IndexOutOfBoundsException e) {
                System.out.println(Display.errorColour(" ❌ Please input a card number (1 to " + numOfCards + "): "));
                System.out.println();
            } catch (IllegalArgumentException e) {
                System.out.println();
            }
        }
        return chosenCardIndex;
    }

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
