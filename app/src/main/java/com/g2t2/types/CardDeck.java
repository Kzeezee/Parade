package com.g2t2.types;

import com.g2t2.enums.CardColour;

import java.io.Serializable;
import java.util.*;

/**
 * CardDeck class, modelling the deck of Parade cards.
 * 
 * @author Ben
 */

public class CardDeck implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Card> deck;

    /**
     * Creates a new CardDeck.
     */
    public CardDeck() {
        deck = new ArrayList<>();
        // initalising all the cards in the deck
        for (int i = 0; i < 11; i++) {
            for (CardColour colour : CardColour.values()) {
                deck.add(new Card(i, colour));
            }
        }
    }

    /**
     * Returns an List of Cards in the deck.
     * 
     * @return the cards in the deck
     */
    public List<Card> getCardsInDeck() {
        return deck;
    }

     /**
     * Returns a random Card from the deck to be drawn.
     * 
     * @return a random card from the deck
     */
    public Card drawCard() {
        // Math.random() generates a random double between 0.0 (inclusive) and 1.0
        // (exclusive)
        int randomNumber = (int) (Math.random() * deck.size());
        Card drawnCard = deck.get(randomNumber);
        deck.remove(randomNumber);
        return drawnCard;
    }

     /**
     * Checks if the deck contains 0 cards.
     * 
     * @return {@code true} if the deck is empty, else {@code false}
     */
    public boolean isEmpty() {
        return deck.size() == 0;
    }
}
