package com.g2t2.util;

import java.util.Map;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;

import java.util.stream.Collectors;

import com.g2t2.enums.CardColour;
import com.g2t2.types.Card;

/**
 * A class for displaying the cards and colored texts
 * 
 * @author Shwe
 */
public class Display {

    /**
     * Displays all stacks grouped by CardColour, with two stacks per row.
     * 
     * @param cards the List of Card containing the stacks to be printed
     */
    public static void displayAllStacks(List<Card> cards) {
        // Group cards by colour
        Collections.sort(cards);
        Map<CardColour, List<Card>> groupedCards = cards.stream()
                .collect(Collectors.groupingBy(Card::getColour));

        // Convert each stack into a string representation
        List<String> stackLines = groupedCards.entrySet().stream()
                .map(entry -> getStackAsString(entry.getValue(), entry.getKey()))
                .collect(Collectors.toList());

        // Print stacks three at a time
        printStacksInGroups(stackLines);
        System.out.println(Constants.ANSI_RESET);

        System.out.println(Constants.DIVIDER);
    }

    /**
     * Returns a single stack of cards as a string representation.
     * 
     * @param cards             the List of Card containing the stack to be printed
     * @param currentCardColour the colour of the stack
     * @return a single stack of cards as a string representation
     */
    private static String getStackAsString(List<Card> cards, CardColour currentCardColour) {
        StringBuilder stackBuilder = new StringBuilder();
        String ansiCodeString = currentCardColour.getAnsiCode();
        stackBuilder.append(ansiCodeString);

        int size = cards.size();

        stackBuilder.append(ansiCodeString + getCardStackLine1(size) + "\n");
        stackBuilder.append(ansiCodeString + getCardStackLine2(cards) + "\n");
        stackBuilder.append(ansiCodeString + getCardStackLine3(size, currentCardColour) + "\n");
        stackBuilder.append(ansiCodeString + getCardStackLine4(size, cards.get(cards.size() - 1).getValue()) + "\n");
        stackBuilder.append(ansiCodeString + getCardStackLine5(size) + "\n");

        stackBuilder.append(Constants.ANSI_RESET);
        return stackBuilder.toString();
    }

    /**
     * Prints stacks in groups (three stacks per row).
     * 
     * @param stackLines List of String, with each element being a string
     *                   representation of a single stack
     */
    private static void printStacksInGroups(List<String> stackLines) {
        String[] emptyLines = new String[Constants.CARD_HEIGHT];
        Arrays.fill(emptyLines, ""); // Initialize empty lines for padding

        for (int i = 0; i < stackLines.size(); i += 3) { // Iterate three stacks at a time
            String[] firstStack = stackLines.get(i).split("\n");
            String[] secondStack = (i + 1 < stackLines.size()) ? stackLines.get(i + 1).split("\n") : emptyLines;
            String[] thirdStack = (i + 2 < stackLines.size()) ? stackLines.get(i + 2).split("\n") : emptyLines;

            for (int j = 0; j < Math.min(firstStack.length, Math.min(secondStack.length, thirdStack.length)); j++) {
                String emptySpace = "  ";
                if (j == 0) {
                    emptySpace = " ";
                }
                System.out.println(firstStack[j] + emptySpace + secondStack[j] + emptySpace + thirdStack[j]);
            }
        }
    }

    /**
     * Returns the first line of a string representation of a card stack.
     * 
     * @param size the number of cards in the stack
     * @return a String representing the first line of a card stack
     */
    private static String getCardStackLine1(int size) {
        StringBuilder line = new StringBuilder("");
        for (int i = 0; i < size; i++) {
            // line.append("___"); // ─────
            line.append("┌──");
        }
        line.append("──────┐ ");
        return line.toString();
    }

    /**
     * Returns the second line of a string representation of a card stack.
     * 
     * @param cards the List of Card in the stack to get the second line from
     * @return a String representing the second line of a card stack
     */
    private static String getCardStackLine2(List<Card> cards) {
        StringBuilder line = new StringBuilder();
        for (Card card : cards) {
            line.append("│" + card.getValue());
            if (card.getValue() != 10) {
                line.append(" ");
            }
        }
        line.append("      │");
        return line.toString();
    }

    /**
     * Returns the 3rd line of a string representation of a card stack.
     * 
     * @param size              the number of cards in the stack
     * @param currentCardColour the colour of the stack
     * @return a String representing the fourth line of a card stack
     */
    private static String getCardStackLine3(int size, CardColour currentCardColour) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < size; i++) {
            line.append("│  ");
        }
        // line.append(" │");
        line.append(" ").append(Constants.EMOJIS.get(currentCardColour.ordinal())).append("   │");
        return line.toString();
    }

    /**
     * Returns the fourth line of a string representation of a card stack.
     * 
     * @param size          the number of cards in the stack
     * @param lastCardValue the value of the topmost card in the stack
     * @return a String representing the fourth line of a card stack
     */
    private static String getCardStackLine4(int size, int lastCardValue) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < size; i++) {
            line.append("│  ");
        }
        // line.append("
        // ").append(Constants.EMOJIS.get(currentCardColour.ordinal())).append(" │");
        line.append("    ");
        if (lastCardValue == 10) {
            line.append(lastCardValue + "│");
        } else {
            line.append(" " + lastCardValue + "│");
        }
        return line.toString();
    }

    /**
     * Returns the fifth line of a string representation of a card stack.
     * 
     * @param size the number of cards in the stack
     * @return a String representing the fifth line of a card stack
     */
    private static String getCardStackLine5(int size) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < size; i++) {
            // line.append("|__");
            line.append("└──");
        }
        // line.append("____");
        line.append("──────┘");
        // if (lastCardValue == 10) {
        // line.append(lastCardValue + "|");
        // } else {
        // line.append("_" + lastCardValue + "|");
        // }
        return line.toString();
    }

    /**
     * Displays all cards side by side in a compact format.
     * 
     * @param cards the List of Card to be displayed as a string representation
     */
    public static void displayCardsSideBySide(List<Card> cards, boolean isAscending) {
        // Generate each card as a string representation
        List<String[]> cardLines = cards.stream()
                .map(Display::getCardAsStringArray)
                .toList();

        // Print the cards row by row (compact format)
        for (int i = 0; i < Constants.CARD_HEIGHT; i++) { // Each card has 6 lines
            for (String[] card : cardLines) {
                System.out.print(card[i] + "  "); // Add spacing between cards
            }
            System.out.println(Constants.ANSI_RESET); // reset the colour
        }
        if (isAscending) {
            for (int i = 0; i < cards.size(); i++) {
                String ansi = cards.get(i).getColour().getAnsiCode();
                System.out.printf(ansi + "  「 %d 」   " + Constants.ANSI_RESET, i + 1);
            } // 「 ✦ Number ✦ 」
        } else {
            for (int i = cards.size(); i > 0; i--) {
                String ansi = cards.get(cards.size() - i).getColour().getAnsiCode();
                System.out.printf(ansi + "  「 %d 」   " + Constants.ANSI_RESET, i);
            } // 「 ✦ Number ✦ 」
        }
        System.out.println();
    }

    /**
     * Returns a String array representing a singular card.
     * Index 0 = top of card
     * Index 5 = bottom of card
     * 
     * @param card the card to be made into a String array
     * @return a String array representing a singular card
     */
    private static String[] getCardAsStringArray(Card card) {
        String[] cardLines = new String[Constants.CARD_HEIGHT];
        int value = card.getValue();
        String ansi = card.getColour().getAnsiCode();
        String emoji = Constants.EMOJIS.get(card.getColour().ordinal());

        // cardLines[0] = ansi + " ________ ";
        // cardLines[1] = ansi + "|" + value + " |";

        // if (value == 10) {
        // cardLines[1] = ansi + "|" + value + " |";
        // }

        // cardLines[2] = ansi + "| |";
        // cardLines[3] = ansi + "| " + emoji + " |";
        // cardLines[4] = ansi + "| |";
        // cardLines[5] = ansi + "|_______" + value + "|";

        // if (value == 10) {
        // cardLines[5] = ansi + "|______" + value + "|";
        // }

        cardLines[0] = ansi + "┌────────┐";

        cardLines[1] = ansi + "│" + value + "       │";
        if (value == 10) {
            cardLines[1] = ansi + "│" + value + "      │";
        }

        cardLines[2] = ansi + "│   " + emoji + "   │";

        cardLines[3] = ansi + "│       " + value + "│";
        if (value == 10) {
            cardLines[3] = ansi + "│      " + value + "│";
        }

        cardLines[4] = ansi + "└────────┘";

        return cardLines;
    }

    /**
     * Wraps the given String in blue ANSI escape codes.
     * 
     * @param s the String to be wrapped
     * @return the given String in blue ANSI escape codes
     */
    public static String colorString(String s) {
        return Constants.BLUE_ANSI + s + Constants.ANSI_RESET;
    }

    /**
     * Wraps the given String in red ANSI escape codes, representing an error.
     * 
     * @param s the String to be wrapped
     * @return the given String in red ANSI escape codes
     */
    public static String errorColour(String s) {
        return Constants.RED_ANSI + s + Constants.ANSI_RESET;
    }

    /**
     * Prints the goodbye banner
     */
    public static void sayGoodBye() {
        System.out.println(Constants.GOODBYE);
    }

    /**
     * Delays the print of the specified text by a specified number of milliseconds.
     * 
     * @param text        the text to be printed
     * @param delayMillis the duration of the delay in milliseconds
     */
    public static void delayPrint(String text, long delayMillis) {
        boolean wasInterrupted = false;

        for (char c : text.toCharArray()) {
            System.out.print(c);
            System.out.flush();

            if (!wasInterrupted) {
                try {
                    Thread.sleep(delayMillis);
                } catch (InterruptedException e) {
                    // Set flag to skip future delays
                    wasInterrupted = true;
                    // Restore interrupt status for proper thread management
                    Thread.currentThread().interrupt();
                }
            }
        }
        System.out.println();
    }

    /**
     * Prints the game header.
     */
    public static void gameHeader() {
        Utility.clearConsoleScreen();
        System.out.println(Constants.BANNER);
        System.out.println(" 📌 To leave the game, enter '" + Constants.LEAVE_GAME + "'");
        System.out.println(Constants.DIVIDER);
    }

    public static void waitingLobbyHeader() {
        Utility.clearConsoleScreen();
        System.out.println(Constants.ONLINE_LOBBY_BANNER);
        System.out.println(Constants.DIVIDER);
        System.out.println(" 📌 To go back, enter: '" + Constants.GO_BACK + "'");
        System.out.println(" 📌 To create a game, enter: '" + Constants.CREATE_GAME + "'");
        System.out.println(" 📌 To join a game, enter: '" + Constants.JOIN_GAME + "'");
    }
}
