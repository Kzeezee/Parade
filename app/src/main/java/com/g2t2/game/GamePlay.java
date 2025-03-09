package com.g2t2.game;

import com.g2t2.types.*;
import com.g2t2.util.*;

import java.util.*;

/**
 * Class managing all the gamePlay
 * @author ben
 */
public class GamePlay {
 
    public static void startGame() {
        // finding a random person to start the game
        int randomNumber = (int) (Math.random() * ParadeBoard.getPlayers().size());
    }

    public static void playTurn(Player player) {
        // clear the console
        Utility.clearConsoleScreen();

        displayCard(player);

    }

    public static void displayCard(Player player) {
        // show the parade collection
        System.out.println("--PARADE COLLECTION--");
        System.out.println("--Cards at the top are the furthest from you--");
        for (int i = 0; i < ParadeBoard.getParade().size(); i++) {
            System.out.println((i+1) + ". " + ParadeBoard.getParade().get(i));
        }
        System.out.println(Constants.DIVIDER);

        // show the player what cards he has in the collection
        System.out.println("--CARDS IN YOUR COLLECTION--");
        for (int i = 0; i < player.getCardsInCollection().size(); i++) {
            System.out.println((i+1) + ". " + player.getCardsInCollection().get(i));
        }
        System.out.println(Constants.DIVIDER);

        // show the player what card he has in his hand
        System.out.println("--CARDS IN YOUR HAND--");
        for (int i = 0; i < player.getCardsOnHand().size(); i++) {
            System.out.println((i+1) + ". " + player.getCardsOnHand().get(i));
        }
        System.out.println(Constants.DIVIDER);
    }

    public static Card placeCardInToTheParade(Player player) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                System.out.println("Which card do you want to place in the Parade?");
                System.out.print("Type the Card Index (1"  );
            } catch (IllegalArgumentException e) {

            }
        }
    }
        
}
