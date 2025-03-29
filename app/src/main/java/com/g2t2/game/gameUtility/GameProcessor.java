package com.g2t2.game.gameUtility;

import java.util.ArrayList;
import java.util.List;

import com.g2t2.game.GameState;
import com.g2t2.types.Card;
import com.g2t2.types.Player;
import com.g2t2.util.Constants;
import com.g2t2.util.Display;

/**
 * Utility class for handling game actions such as playing turns,
 * choosing cards, and managing collections.
 * 
 * @author Ben
 * @author Shwe
 * @author Easan
 */
public class GameProcessor {
    
    public static void processPlayerTurn(int index, Player currentPlayer, GameState game) {
        processCardChosenToPutToParade(index, currentPlayer, game);
    }
    
    /**
     * Executes a player's turn in the game.
     *
     * @param player The player whose turn it is.
     * @param game   The current game state.
     */
    public static void playTurn(Player player, GameState game) {
        if (player.getCPU()) {
            CPUGameAction.CPUplayTurn(player, game);
        } else {
            playerplayTurn(player, game);
        }
    }

    /**
     * Handles a human player's turn.
     *
     * @param player The human player.
     * @param game   The current game state.
     */
    private static void playerplayTurn(Player player, GameState game) {
        GameRenderer.displayRoundInformation(game);
        GameRenderer.displayHandCards(player);

        int cardIdx = Prompt.promptForCard(Constants.PROMPT_ADD_TO_PARADE, player);
        processCardChosenToPutToParade(cardIdx, player, game);
    }

    /**
     * Handles a CPU player's turn.
     *
     * @param player The CPU player.
     * @param game   The current game state.
     */
    protected static void processCardChosenToPutToParade(int cardIdx, Player player, GameState game) {
        Card chosenCard = player.getCardsOnHand().get(cardIdx);
        player.getCardsOnHand().remove(chosenCard);

        List<Card> cardsCollected = adjustParadeBoard(chosenCard, game);
        player.getCardsInCollection().addAll(cardsCollected);
        if (!game.isLastRound()) {
            player.drawCard(game.getDeck());
        }
    }

    /**
     * Adjusts the parade board after a card is placed and returns
     * the cards to be added to the player's collection.
     *
     * @param inputCard The card being placed in the parade.
     * @param game      The current game state.
     * @return A list of cards collected by the player.
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
     * @param inputCard The card being placed in the parade.
     * @param game      The current game state.
     * @return A list of collectable cards from the parade.
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
     * @param input    The card being placed in the parade.
     * @param inParade The card currently in the parade.
     * @return True if the card is collectible; false otherwise.
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
    public static void putTwoToCollections(Player player, GameState game) {
        GameRenderer.displayRoundInformation(game);

        List<Integer> cardIdxList = null;
        if (player.getCPU()) {
            Display.delayPrint(" 📌 Player " + Display.colorString(player.getName())
                    + " is choosing their cards.", 30);
            cardIdxList = CPUGameAction.CPUSelectTwoToCollections(player, game);
        } else {
            System.out.println(" ⚜ You need to select " + Display.colorString("TWO")
                    + " cards from your hand to add to the collection.");

            cardIdxList = Prompt.promptToCollectTwo(player, game);
        }
        System.out.println(Constants.DIVIDER);

        processCardChosenToCollection(cardIdxList, player);
    }

    /**
     * Processes a list of selected cards and adds them to a player's collection.
     *
     * @param cardIdxList A list of indices representing selected cards from the
     *                    hand.
     * @param player      The player whose collection is being updated.
     */
    private static void processCardChosenToCollection(List<Integer> cardIdxList, Player player) {
        for (Integer idx : cardIdxList) {
            Card card = player.getCardsOnHand().get(idx);
            player.getCardsOnHand().remove(card);
            player.getCardsInCollection().add(card);
        }
    }

}
