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
        int currentPlayer = (int) (Math.random() * ParadeBoard.getPlayers().size());
        int previousPlayer = currentPlayer;

        // checks the previous player because they are the one that might have a
        // complete collection (ALL COLOURS)
        while (!ScoreCalculation.meetsGameEndCondition(ParadeBoard.getPlayers().get(previousPlayer),
                ParadeBoard.getDECK())) {

            playTurn(ParadeBoard.getPlayers().get(currentPlayer), false);
            previousPlayer = currentPlayer;
            currentPlayer = (currentPlayer + 1) % ParadeBoard.getPlayers().size();
        }
        // last round of play
        for (int i = 0; i < ParadeBoard.getPlayers().size(); i++) {
            playTurn(ParadeBoard.getPlayers().get(currentPlayer), true);
            currentPlayer = (currentPlayer + 1) % ParadeBoard.getPlayers().size();
        }

        // choose 2 cards in hand to add to collection
        for (int i = 0; i < ParadeBoard.getPlayers().size(); i++) {
            putTwoToCollections(ParadeBoard.getPlayers().get(currentPlayer));
            currentPlayer = (currentPlayer + 1) % ParadeBoard.getPlayers().size();
        }
        ScoreCalculation.computeScore(ParadeBoard.getPlayers());
        displayScores(ParadeBoard.getPlayers());

        promptForNewGame();
    }

    // ask whether the player wants to play another round
    private static void promptForNewGame() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                System.out.println(" 📌 The game has ended. 📌 ");
                System.out.println(Display.colorString(" ⚜ Do you want to play another game? (y/n) ➢ "));
                String confirmation = sc.nextLine();

                if (confirmation.equalsIgnoreCase("y")) {
                    StartMenu.showStartMenu();
                } else if (confirmation.equalsIgnoreCase("n")) {
                    Display.sayGoodBye();
                    System.exit(0);
                }
                System.out.println(Constants.ANSI_RESET);
            } catch (IllegalArgumentException e) {
                System.out.println();
            }
        }

        // StartMenu.showStartMenu();
    }

    // player select two cards from hand to collection
    private static void putTwoToCollections(Player player) {
        // clear the console
        Utility.clearConsoleScreen();
        System.out.println(Constants.BANNER);
        System.out.println(Constants.DIVIDER);

        displayCollections();
        displayParade();
        System.out.println(" 📌 Currently, it is player " + Display.colorString(player.getName()) + "(" +
                Display.colorString("" + player.getPlayerId()) + ") turn.");
        System.out.println(" ⚜ You need to select " + Display.colorString("TWO")
                + " cards from your hand to add to the collection.");
        System.out.println(Constants.DIVIDER);

        addSelectedCardsToParade(player);
    }

    private static void playTurn(Player player, boolean isLastRound) {
        // clear the console
        Utility.clearConsoleScreen();
        System.out.println(Constants.BANNER);
        System.out.println(Constants.DIVIDER);

        displayCollections();
        displayParade();
        System.out.println(" 📌 Number of Cards Left in the Deck: " + ParadeBoard.getDECK().getCardsInDeck().size());
        System.out.println(" 📌 Currently, it is player " + Display
                .colorString(player.getName() + "(" + player.getPlayerId() + ")" + Constants.ANSI_RESET + " turn."));
        if (isLastRound) {
            System.out.println(" ⚜ " + Display.colorString(" 📌 LAST ROUND 📌 ") + Constants.ANSI_RESET);
        }
        System.out.println(Constants.DIVIDER);

        displayHandCards(player);

        String prompt = String.format(" 📌 Which card do you want to place in the Parade?%n");
        Card chosenCard = promptForCard(prompt, player);
        System.out.println(Constants.ANSI_RESET);
        player.getCardsOnHand().remove(chosenCard);

        ArrayList<Card> cardsCollected = adjustParadeBoard(chosenCard);
        addToCollection(cardsCollected, player);
        if (!isLastRound) {
            player.drawCard(ParadeBoard.getDECK());
        }
    }

    // print out the prompt string and accordingly ask the player for valid entry
    private static Card promptForCard(String prompt, Player player) {
        Card chosenCard = null;
        Scanner sc = new Scanner(System.in);
        int numOfCards = player.getCardsOnHand().size();
        while (true) {
            try {
                System.out.print(prompt);
                System.out.print(Display.colorString(" ⚜ Choose card number (1 to " + numOfCards + ") or 'q' to quit ➢ "));
                // int chosenCardIndex = sc.nextInt() - 1;
                String chosenCardString = sc.nextLine();
                if (chosenCardString.equalsIgnoreCase("q")) {
                    Display.sayGoodBye();
                    System.exit(0);
                }
                int chosenCardIndex = Integer.parseInt(chosenCardString);
                chosenCard = player.getCardsOnHand().get(chosenCardIndex);

                // Confirmation on card choice
                System.out.println(Constants.ANSI_RESET + " 📌 Chosen Card Number: " + (chosenCardIndex + 1));
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
        return chosenCard;

    }

    // function to prompt for selected cards and add to the Collections from hand
    private static void addSelectedCardsToParade(Player player) {
        ArrayList<Card> cardsSelected = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            displayHandCards(player);
            String prompt = Display.colorString(" 📌 Which card do you want to place in the Parade?\n" +
                    " 📌 You need to choose TWO cards \n" +
                    " ⚜ Card " + (i + 1) + " :");
            Card selectedCard = promptForCard(prompt, player);
            player.getCardsOnHand().remove(selectedCard);
            player.getCardsInCollection().add(selectedCard);
            cardsSelected.add(selectedCard);
            System.out.println(Constants.DIVIDER);
        }
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

    // show collections of all players
    private static void displayCollections() {
        for (Player p : ParadeBoard.getPlayers()) {
            System.out.println(
                    Display.colorString("「 ✦ CARDS IN " + p.getName() + "(" + p.getPlayerId() + ") COLLECTION ✦ ") + Constants.ANSI_RESET);
            Display.displayAllStacks(p.getCardsInCollection());
        }
    }

    // show the parade collection
    private static void displayParade() {
        System.out.println(Display.colorString("「 ✦ PARADE COLLECTION ✦ 」" + Constants.ANSI_RESET));
        System.out.println("The front of the Parade starts from the left");
        Display.displayCardsSideBySide(ParadeBoard.getParade());
        System.out.println(Constants.DIVIDER);
    }

    // show the player what card he has in his hand
    private static void displayHandCards(Player player) {
        System.out.println(Display.colorString("「 ✦ CARDS IN YOUR HAND ✦ 」") + Constants.ANSI_RESET);
        System.out.println("Each card is numbered, starting from the left, 1");
        Display.displayCardsSideBySide(player.getCardsOnHand());
        System.out.println(Constants.DIVIDER);
    }

    private static void displayScores(ArrayList<Player> players) {

        System.out.println(Constants.DIVIDER);
        System.out.println(Display.colorString("「 ✦ RANKING ✦ 」") + Constants.ANSI_RESET);
        // Sort players by score in ascending order (lowest score first)
        List<Player> sortedPlayers = players.stream()
                .sorted(Comparator.comparingInt(Player::getScore)) // Sort by score in ascending order
                .toList();

        // Assign ranks based on sorted order
        int rank = 1;
        for (Player player : sortedPlayers) {
            System.out.printf("%d. Player %d, %s, Score: [%d]%n", rank, player.getPlayerId(), player.getName(),
                    player.getScore());
            rank++;
        }

        System.out.println(Constants.DIVIDER);
    }

}
