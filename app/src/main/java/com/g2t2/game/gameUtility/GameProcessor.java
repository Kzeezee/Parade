package com.g2t2.game.gameUtility;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import com.g2t2.game.GameState;
import com.g2t2.types.Card;
import com.g2t2.types.ParadeBoard;
import com.g2t2.types.Player;
import com.g2t2.util.Constants;
import com.g2t2.util.Display;


public class GameProcessor {
    // remove card from player hand, adjust parade board, add cards to collection, player draw card
    public static void processPlayerTurn(int cardIndex, Player player,  GameState game) {        
        Card chosenCard = player.getCardsOnHand().get(cardIndex);
        player.getCardsOnHand().remove(chosenCard);

        List<Card> cardsCollected = adjustParadeBoard(chosenCard, game);
        addToCollection(cardsCollected, player);
        if (game.isLastRound()) {
            player.drawCard(game.getDeck());
        }    
    }

    // returns the cards to be added to the player's collection
    private static List<Card> adjustParadeBoard(Card inputCard, GameState game) {
        List<Card> toCollection = new ArrayList<>();
        int cardValue = inputCard.getValue();
        for (int i = 0; i < game.getParade().size() - cardValue; i++) {

            // check if need to add to collection
            if (isCollectable(inputCard, ParadeBoard.getParade().get(i))) {
                toCollection.add(game.getParade().get(i));
                game.getParade().remove(i);
                i--; // because remove shifts the whole collection backwards by 1
            }
        }

        // add card to the parade
        game.getParade().add(inputCard);
        return toCollection;
    }

    //  Returns the collectable cards of a card to be placed.
    private static List<Card> getAllCollectableCards(Card inputCard, GameState game) {
        int cardValue = inputCard.getValue();
        List<Card> collectableCards = new ArrayList<>();
        List<Card> cardsInParade = game.getParade();

        for (int i = 0; i < cardsInParade.size() - cardValue; i++) {
            if (isCollectable(inputCard, cardsInParade.get(i))) {
                collectableCards.add(cardsInParade.get(i));
            }
        }

        return collectableCards;
    }

    // return whether the card in parade is collectible based on the input card
    private static boolean isCollectable(Card input, Card inParade) {
        if (input.getColour().equals(inParade.getColour())) {
            return true;
        }

        if (inParade.getValue() <= input.getValue()) {
            return true;
        }

        return false;
    }

    // add the card to the player's collection
    private static void addToCollection(List<Card> cardsToAdd, Player player) {
        for (Card c : cardsToAdd) {
            player.getCardsInCollection().add(c);
        }
    }
  
    // player select two cards from hand to collection
    private static void putTwoToCollections(Player player, GameState game) {
        // clear the console
        GameRenderer.displayRoundInformation(game);
       
        System.out.println(" ⚜ You need to select " + Display.colorString("TWO")
                + " cards from your hand to add to the collection.");
        System.out.println(Constants.DIVIDER);

            addSelectedCardsToParade(player);
    }

    // function to prompt for selected cards and add to the Collections from hand
    private static void addSelectedCardsToParade(Player player) {
        List<Card> cardsSelected = new ArrayList<>();
        for (int i = 0; i < 2; i++) { // prompt for 2 cards and add to collection
            GameRenderer.displayHandCards(player);

            String prompt = Display.colorString(" 📌 Which card do you want to place in the Collection?\n" +
                    " 📌 You need to choose TWO cards \n" +
                    " ⚜ Card " + (i + 1) + " :");
            int selectedCardIndex = Prompt.promptForCard(player);
            Card selectedCard = player.getCardsOnHand().get(selectedCardIndex);

            player.getCardsOnHand().remove(selectedCard);
            player.getCardsInCollection().add(selectedCard);
            cardsSelected.add(selectedCard);

            System.out.println(Constants.DIVIDER);
        }
    }
    
    // Calculates the total amount of points gained if a specific card were to be played
    private static int calculateTotalPoints(List<Card> collectableCards) {
        int totalPoints = 0;
        for (Card card : collectableCards) {
            totalPoints += card.getValue();
        }

        return totalPoints;
    }

    // CPU THINGS --------
    
    // CPU version of playTurn() method
    private static void CPUturn(Player player, GameState game){
        GameRenderer.displayRoundInformation(game);
        Display.delayPrint(" 📌 Player " + Display.colorString(player.getName() 
        + Constants.ANSI_RESET + " is choosing their card."),30);

        Card chosenCard = chooseCPUCard(player, 5, game);
        System.out.println(Constants.ANSI_RESET);
        player.getCardsOnHand().remove(chosenCard);

        List<Card> cardsCollected = adjustParadeBoard(chosenCard, game);
        addToCollection(cardsCollected, player);

        if (game.isLastRound()) {
            player.drawCard(game.getDeck());
        }
    }

    // choose which card based on CPU difficulty
    private static Card chooseCPUCard(Player player, int numOfCards, GameState game) {
        switch (player.getDifficulty()) { 
            // level 1 difficulty: random
            case 1:
                return player.getCardsOnHand().get((int) (Math.random() * numOfCards));
            // level 2 difficulty: picks the card that gives back the lowest number of cards
            // from the parade
            case 2:
                return chooseMediumDifficultyCard(player, game);
            case 3:
                return chooseHardDifficultyCard(player, game);
            default:
                return player.getCardsOnHand().get((int) (Math.random() * numOfCards));
        }
    }

    // Chooses a card for the medium level difficulty bot to play
    private static Card chooseMediumDifficultyCard(Player player, GameState game) {
        List<Card> hand = player.getCardsOnHand();
        int lowestNumCollect = Integer.MAX_VALUE;
        Card bestCard = player.getCardsOnHand().get(0);

        for (Card card : hand) {
            List<Card> collectableCards = getAllCollectableCards(card, game);
            if (collectableCards.size() < lowestNumCollect) {
                lowestNumCollect = collectableCards.size();
                bestCard = card;
            }
        }
        return bestCard;
    }

    // Chooses a card for the hard level difficulty bot to play
    private static Card chooseHardDifficultyCard(Player player, GameState game) {
        List<Card> hand = player.getCardsOnHand();
        int lowestPoints = Integer.MAX_VALUE;
        Card bestCard = null; 

        for (Card card : hand) {
            List<Card> collectableCards = getAllCollectableCards(card, game);
            int totalPoints = calculateTotalPoints(collectableCards);

            if (totalPoints < lowestPoints) {
                lowestPoints = totalPoints;
                bestCard = card;
            }
        }

        return bestCard;
    }

    // CPU version of the putTwoToCollections() method
    private static void putTwoToCPUCollections(Player player, GameState game){
        System.out.println(" 📌 Currently, it is " + Display.colorString(player.getName()) + "'s turn.");
        System.out.println(Constants.DIVIDER);
        System.out.println(" 📌 Player " + Display.colorString(player.getName() 
        + Constants.ANSI_RESET + " is choosing their cards."));
        try {
            Thread.sleep(2000);  
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Card> cardsSelected = new ArrayList<>();
        for (int i = 0; i < 2; i++){
            Card selectedCard = chooseCPUCard(player, 4 - i, game);
            player.getCardsOnHand().remove(selectedCard);
            player.getCardsInCollection().add(selectedCard);
            cardsSelected.add(selectedCard);
            System.out.println(Constants.DIVIDER);
        }
    }

}
