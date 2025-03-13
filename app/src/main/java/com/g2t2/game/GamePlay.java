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
        int currentPlayer = (int) (Math.random() * ParadeBoard.getPlayers().size());
        int previousPlayer = currentPlayer;
        boolean draw = true;

        // checks the previous player because they are the one that might have a complete collection (ALL COLOURS)
        while (!ScoreCalculation.meetsGameEndCondition(ParadeBoard.getPlayers().get(previousPlayer), ParadeBoard.getDECK())) {
            playTurn(ParadeBoard.getPlayers().get(currentPlayer), draw);
            previousPlayer = currentPlayer;
            currentPlayer = (currentPlayer + 1) % ParadeBoard.getPlayers().size();
        }
        //started implementing endgame logic - Easan

        // ensure no cards are drawn in final round
        draw = false;

        System.out.println(Constants.DIVIDER);
        System.out.println("Endgame condition met. Entering final round.");
        System.out.println(Constants.DIVIDER);

        // ensures message is seen before console is cleared in the playTurn function
        try {
            Thread.sleep(5000); // Pause for 5 seconds before clearing
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Ensure all players get one last turn
        for (int i = 0; i < ParadeBoard.getPlayers().size(); i++) {
            int playerIndex = (currentPlayer + i) % ParadeBoard.getPlayers().size();
            playTurn(ParadeBoard.getPlayers().get(playerIndex), draw);
        }

        System.out.println(Constants.DIVIDER);
        System.out.println("Choose 2 cards for your collection");
        System.out.println(Constants.DIVIDER);

        // ensures message is seen before console is cleared in the chooseTwo function
        try {
            Thread.sleep(5000); // Pause for 5 seconds before clearing
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Choose 2 cards for the collection
        for (int i = 0; i < ParadeBoard.getPlayers().size(); i++) {
            int playerIndex = (currentPlayer + i) % ParadeBoard.getPlayers().size();
            chooseTwo(ParadeBoard.getPlayers().get(playerIndex));
        }
        
        //@author chue
    //     Compute final scores and determine the winner
    // Map<Player, Integer> finalScores = ScoreCalculation.computeScore(ParadeBoard.getPlayers());
    // Player winner = WinnerCalculation.findWinner(finalScores);

    // System.out.println(Constants.DIVIDER);
    // System.out.println("The winner is " + winner.getName() + " with a score of " + finalScores.get(winner) + "!");
    // System.out.println(Constants.DIVIDER);
    }

   /**
    * Method for choosing 2 cards to keep for scoring
    * @author Easan
    */
    private static void chooseTwo(Player player) {
        // clear the console
        Utility.clearConsoleScreen();
        displayCollections();

        System.out.println(Constants.DIVIDER);
        System.out.println("Currently, it is player " + player.getName() + "(" + player.getPlayerId() + ") turn.");
        System.out.println(Constants.DIVIDER);

        displayHandCards(player);

        
        ArrayList<Card> collection = new ArrayList<>();
        Card cardsCollected = placeCard(player, "Which is the first card you want to place in your collection?");
        collection.add(cardsCollected);

        Utility.clearConsoleScreen();
        cardsCollected = placeCard(player, "Which is the second card you want to place in your collection?");
        collection.add(cardsCollected);
        addToCollection(collection, player);
    }

    private static void playTurn(Player player, boolean draw) {
        // clear the console
        Utility.clearConsoleScreen();

        displayCollections();
        displayParade();
        System.out.println(Constants.DIVIDER);
        System.out.println("Number of Cards Left in the Deck: " + ParadeBoard.getDECK().getCardsInDeck().size());
        System.out.println("Currently, it is player " + player.getName() + "(" + player.getPlayerId() + ") turn.");
        System.out.println(Constants.DIVIDER);
        
        displayHandCards(player);

        Card chosenCard = placeCard(player, "Which card do you want to place in the Parade?");
        ArrayList<Card> cardsCollected = adjustParadeBoard(chosenCard);
        addToCollection(cardsCollected, player);
        if (draw){
            player.drawCard(ParadeBoard.getDECK());
        }

    }

    // show collections of all players
    private static void displayCollections() {
        for (Player p : ParadeBoard.getPlayers()) {
            System.out.println("--CARDS IN " + p.getName() + "(" + p.getPlayerId() + ") COLLECTION--");
            Collections.sort(p.getCardsInCollection());
            for (int i = 0; i < p.getCardsInCollection().size(); i++) {
                System.out.println((i+1) + ". " + p.getCardsInCollection().get(i));
            }
            System.out.println(Constants.DIVIDER);
        }
    }

    // show the parade collection
    private static void displayParade() {
        System.out.println("--PARADE COLLECTION--");
        System.out.println("-Cards at the top are the furthest from you-");
        for (int i = 0; i < ParadeBoard.getParade().size(); i++) {
            System.out.println((i+1) + ". " + ParadeBoard.getParade().get(i));
        }
    }

    // show the player what card he has in his hand
    private static void displayHandCards(Player player) {
        System.out.println("--CARDS IN YOUR HAND--");
        Collections.sort(player.getCardsOnHand());
        for (int i = 0; i < player.getCardsOnHand().size(); i++) {
            System.out.println((i+1) + ". " + player.getCardsOnHand().get(i));
        }
        System.out.println(Constants.DIVIDER);
    }

    private static Card placeCard(Player player, String msg) {
        Scanner sc = new Scanner(System.in);
        int numOfCards = player.getCardsOnHand().size();
        Card chosenCard = null;
        while (true) {
            try {
                System.out.println(msg);
                System.out.print("Choose card number (1 to " + numOfCards + "): ");
                int chosenCardIndex = sc.nextInt() - 1;
                sc.nextLine(); 
                chosenCard = player.getCardsOnHand().get(chosenCardIndex);

                // Confirmation on card choice
                System.out.println("Chosen Card Number: " + (chosenCardIndex + 1));
                System.out.print("Enter 'y' to confirm: ");
                String confirmation = sc.nextLine();

                if (confirmation.length() != 1 || !Character.isLetter(confirmation.charAt(0)) || Character.toLowerCase(confirmation.charAt(0)) != 'y') {
                    System.out.println();
                    continue;
                }

                player.getCardsOnHand().remove(chosenCardIndex);
                break;
            } catch (InputMismatchException e) {
                System.out.println("Please input a number");
                System.out.println();
                sc.nextLine();
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Please input a card number between (1 to " + numOfCards + ")");
                System.out.println();
            } catch (IllegalArgumentException e) {
                System.out.println();
            }
        }
        return chosenCard;
    }
    
    // returns the cards to be added to the player's collection
    private static ArrayList<Card> adjustParadeBoard(Card inputCard) {
        ArrayList<Card> toCollection = new ArrayList<>();
        int cardValue = inputCard.getValue();
        for (int i = 0; i < ParadeBoard.getParade().size() - cardValue; i++) {

            // check if need to add to collection
            if (isCollectable(inputCard, ParadeBoard.getParade().get(i))) {
                toCollection.add(ParadeBoard.getParade().get(i));
                ParadeBoard.getParade().remove(i);
                i--; // because remove shifts the whole collection backwards by 1
            }
        }

        // add card to the parade
        ParadeBoard.getParade().add(inputCard);
        return toCollection;
    }

    private static boolean isCollectable(Card input, Card inParade) {
        if (input.getColour().equals(inParade.getColour())) {
            return true;
        }

        if (inParade.getValue() <= input.getValue()) {
            return true;
        }

        return false;
    }

    private static void addToCollection(ArrayList<Card> cardsToAdd, Player player) {
        for (Card c : cardsToAdd) {
            player.getCardsInCollection().add(c);
        }
    }
}
