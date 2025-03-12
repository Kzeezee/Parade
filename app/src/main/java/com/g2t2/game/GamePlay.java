package com.g2t2.game;

import com.g2t2.types.*;
import com.g2t2.util.*;

import java.util.*;

/**
 * Class managing all the gamePlay
 * 
 * @author ben
 */
public class GamePlay {

    public static void startGame() {
        // finding a random person to start the game
        int randomNumber = (int) (Math.random() * ParadeBoard.getPlayers().size());
        int previousPlayer = randomNumber;

        // checks the previous player because they are the one that might have a complete collection (ALL COLOURS)
        while (!ScoreCalculation.meetsGameEndCondition(ParadeBoard.getPlayers().get(previousPlayer), ParadeBoard.getDECK())) {
            
            playTurn(ParadeBoard.getPlayers().get(randomNumber), false);
            previousPlayer = randomNumber;
            randomNumber = (randomNumber + 1) % ParadeBoard.getPlayers().size();
        }
        // last round of play
        for (int i = 0; i < ParadeBoard.getPlayers().size(); i++) {
            playTurn(ParadeBoard.getPlayers().get(randomNumber), true);
            randomNumber = (randomNumber + 1) % ParadeBoard.getPlayers().size();
        }

        // choose 2 cards in hand to add to collection
        for (int i = 0; i < ParadeBoard.getPlayers().size(); i++) {
            putTwoToCollections(ParadeBoard.getPlayers().get(randomNumber));
            randomNumber = (randomNumber + 1) % ParadeBoard.getPlayers().size();
        }
        ScoreCalculation.computeScore(ParadeBoard.getPlayers());
        displayScores(ParadeBoard.getPlayers());
    }

    // player select two cards from hand to collection
    private static void putTwoToCollections(Player player) {
        // clear the console
        Utility.clearConsoleScreen();

        displayCollections();
        displayParade();
        System.out.println(Constants.DIVIDER);
        System.out.println("Currently, it is player " + player.getName() + "(" + player.getPlayerId() + ") turn.");
        System.out.println("You need to select TWO cards from your hand to add to the collection.");
        System.out.println(Constants.DIVIDER);

        addSelectedCardsToParade(player);
    }

    private static void playTurn(Player player, boolean isLastRound) {
        // clear the console
        Utility.clearConsoleScreen();

        displayCollections();
        displayParade();
        System.out.println(Constants.DIVIDER);
        System.out.println("Number of Cards Left in the Deck: " + ParadeBoard.getDECK().getCardsInDeck().size());
        System.out.println("Currently, it is player " + player.getName() + "(" + player.getPlayerId() + ") turn.");
        if (isLastRound) {
            System.out.println("This is the last round of Parade");
        }
        System.out.println(Constants.DIVIDER);

        displayHandCards(player);

        String prompt = String.format("Which card do you want to place in the Parade?%n");
        Card chosenCard = promptForCard(prompt, player);
        player.getCardsOnHand().remove(chosenCard);

        // Card chosenCard = placeCardInToTheParade(player);
        ArrayList<Card> cardsCollected = adjustParadeBoard(chosenCard);
        addToCollection(cardsCollected, player);
        if (!isLastRound) {
            player.drawCard(ParadeBoard.getDECK());
        }
    }

    // show collections of all players
    private static void displayCollections() {
        for (Player p : ParadeBoard.getPlayers()) {
            System.out.println("--CARDS IN " + p.getName() + "(" + p.getPlayerId() + ") COLLECTION--");
            Collections.sort(p.getCardsInCollection());
            for (int i = 0; i < p.getCardsInCollection().size(); i++) {
                System.out.println((i + 1) + ". " + p.getCardsInCollection().get(i));
            }
            System.out.println(Constants.DIVIDER);
        }
    }

    // show the parade collection
    private static void displayParade() {
        System.out.println("--PARADE COLLECTION--");
        System.out.println("-Cards at the top are the furthest from you-");
        for (int i = 0; i < ParadeBoard.getParade().size(); i++) {
            System.out.println((i + 1) + ". " + ParadeBoard.getParade().get(i));
        }
    }

    // show the player what card he has in his hand
    private static void displayHandCards(Player player) {
        System.out.println("--CARDS IN YOUR HAND--");
        Collections.sort(player.getCardsOnHand());
        for (int i = 0; i < player.getCardsOnHand().size(); i++) {
            System.out.println((i + 1) + ". " + player.getCardsOnHand().get(i));
        }
        System.out.println(Constants.DIVIDER);
    }

    // print out the prompt string and accordingly ask the player for valid entry
    private static Card promptForCard(String prompt, Player player) {
        Card chosenCard = null;
        Scanner sc = new Scanner(System.in);
        int numOfCards = player.getCardsOnHand().size();
        while (true) {
            try {
                System.out.print(prompt);
                System.out.print("Choose card number (1 to " + numOfCards + "): ");
                int chosenCardIndex = sc.nextInt() - 1;
                sc.nextLine();
                chosenCard = player.getCardsOnHand().get(chosenCardIndex);

                // Confirmation on card choice
                System.out.println("Choosen Card Number: " + (chosenCardIndex + 1));
                System.out.print("Enter 'y' to confirm: ");
                String confirmation = sc.nextLine();
                if (confirmation.length() > 1 || !Character.isLetter(confirmation.charAt(0))
                        || Character.toLowerCase(confirmation.charAt(0)) != 'y') {
                    System.out.println();
                    continue;
                }
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

    // function to prompt for selected cards and add to the Collections from hand
    private static void addSelectedCardsToParade(Player player) {
        ArrayList<Card> cardsSelected = new ArrayList<>();        
        for (int i = 0; i < 2; i++) {
            displayHandCards(player);
            String prompt = String.format("Which card do you want to place in the Parade? You need to choose TWO cards %nCard %d: ", (i + 1));
            Card selectedCard = promptForCard(prompt, player);
            player.getCardsOnHand().remove(selectedCard);
            player.getCardsInCollection().add(selectedCard);
            cardsSelected.add(selectedCard);
        }
        System.out.println(Constants.DIVIDER);
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

    private static void displayScores(ArrayList<Player> players) {
        
        System.out.println(Constants.DIVIDER);
        players.stream()
           .sorted(new PlayerComparator()) // Sort using the comparator
           .forEachOrdered(player -> {
               int index = players.indexOf(player) + 1; // Calculate rank 
               System.out.printf("%d. Player %d, %s, Score: [%d]%n", index, player.getPlayerId(), player.getName(), player.getScore());
           });
        System.out.println(Constants.DIVIDER);
    }

}
