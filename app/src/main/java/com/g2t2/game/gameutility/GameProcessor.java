package com.g2t2.game.gameutility;

import java.util.ArrayList;
import java.util.List;

import com.g2t2.game.GameState;
import com.g2t2.types.Card;
import com.g2t2.types.Player;

/**
 * Utility class for handling game actions such as playing turns,
 * choosing cards, and managing collections.
 * 
 * @author Ben
 * @author Shwe
 * @author Easan
 */
public class GameProcessor {
    /**
     * Executes a player's turn in the game.
     *
     * @param player the player whose turn it is
     * @param game   the current game state
     */
    // public static void playTurn(Player player, GameState game) {
    //     if (player.getCPU()) {
    //         CPUGameAction.CPUplayTurn(player, game);
    //     } else {
    //         playerplayTurn(player, game);
    //     }
    // }

    /**
     * Handles a human player's turn.
     *
     * @param player the human player
     * @param game   the current game state
     */
    // private static void playerplayTurn(Player player, GameState game) {
    //     GameRenderer.displayRoundInformation(game);
    //     GameRenderer.displayHandCards(player);

    //     // String prompt = String.format(Constants.ANSI_RESET + " 📌 Which card do you want to place in the Parade?");
    //     int cardIdx = Integer.parseInt(Prompt.promptForCard(player));
    //     processPlayerTurn(cardIdx, player, game);
    // }

       /**
     * Update the game based on what the user have done
     *
     * @param cardIdx The index of the chosen card
     * @param player The player that completed the turn
     * @param game   The current game state.
     */
    public static void processPlayerTurn(int cardIdx, Player player, GameState game) {
        Card chosenCard = player.getCardsOnHand().get(cardIdx);
        player.getCardsOnHand().remove(chosenCard);

        List<Card> cardsCollected = adjustParadeBoard(chosenCard, game);
        player.getCardsInCollection().addAll(cardsCollected);
        if (!game.isLastRound()) {
            player.drawCard(game.getDeck());
        }
        player.updateCollectionSize();
    }

    /**
     * Adjusts the parade board after a card is placed and returns
     * the cards to be added to the player's collection.
     *
     * @param inputCard the card being placed in the parade
     * @param game      the current game state
     * @return a list of cards collected by the player
     */
    private static List<Card> adjustParadeBoard(Card inputCard, GameState game) {
        List<Card> toCollection = new ArrayList<>();
        int cardValue = inputCard.getValue();
        List<Card> parade = game.getParade();
        for (int i = 0; i < parade.size() - cardValue; i++) {
            // check if need to add to collection
            if (isCollectable(inputCard, parade.get(i))) {
                toCollection.add(parade.get(i));
                parade.remove(i);
                i--; // because remove shifts the whole collection backwards by 1
            }
        }

        // add card to the parade
        parade.add(inputCard);
        return toCollection;
    }

    /**
     * Returns a list of collectable cards based on an input card and the current
     * parade state.
     *
     * @param inputCard the card being placed in the parade
     * @param game      the current game state
     * @return a list of collectable cards from the parade
     */
    protected static List<Card> getCollectableCards(Card inputCard, GameState game) {
        int cardValue = inputCard.getValue();
        List<Card> collectableCards = new ArrayList<>();
        List<Card> parade = game.getParade();

        for (int i = 0; i < parade.size() - cardValue; i++) {
            if (isCollectable(inputCard, parade.get(i))) {
                collectableCards.add(parade.get(i));
            }
        }

        return collectableCards;
    }

    /**
     * Determines whether a card in the parade is collectible based on an input
     * card.
     *
     * @param input    the card being placed in the parade
     * @param inParade the card currently in the parade
     * @return true if the card is collectible; false otherwise
     */
    private static boolean isCollectable(Card input, Card inParade) {
        if (input.getColour().equals(inParade.getColour())) {
            return true;
        }
        if (inParade.getValue() <= input.getValue()) {
            return true;
        }
        return false;
    }

    /**
     * Allows a player to select two cards from their hand to add to their
     * collection.
     *
     * @param player The player selecting cards.
     * @param game   The current game state.
     */
    // public static void putTwoToCollections(Player player, GameState game) {
    //     GameRenderer.displayRoundInformation(game);

    //     String[] cardIdxToAddToCollection = null;
    //     int[] cards = null;
    //     if (player.getCPU()) {
    //         Display.delayPrint(" 📌 Player " + Display.colorString(player.getName())
    //                 + " is choosing their cards.", 30);
    //         cards = CPUGameAction.CPUSelectTwoToCollections(player, game);
    //     } else {
    //         System.out.println(" ⚜ You need to select " + Display.colorString("TWO")
    //                 + " cards from your hand to add to the collection.");

    //         cardIdxToAddToCollection = Prompt.promptToCollectTwo(player);
    //     }
    //     System.out.println(Constants.DIVIDER);
    //     cards = new int[] {Integer.parseInt(cardIdxToAddToCollection[0]), Integer.parseInt(cardIdxToAddToCollection[1])};
    //     processCardChosenToCollection(cards, player);
    // }

    /**
     * Processes the 2 cards selected by the player in the put 2 cards to collection phase
     * and adds them to a player's collection.
     *
     * @param cardIdxList An int[] containting of indices representing selected cards from the hand.
     *                    
     * @param player      The player whose collection is being updated.
     */
    public static void processCardChosenToCollection(int[] cardIdxToAddToCollection, Player player) {
        Card[] cards = {player.getCardsOnHand().get(cardIdxToAddToCollection[0]), player.getCardsOnHand().get(cardIdxToAddToCollection[1])};
        for (Card card : cards) {
            player.getCardsOnHand().remove(card);
            player.getCardsInCollection().add(card);
        }
    }

}
