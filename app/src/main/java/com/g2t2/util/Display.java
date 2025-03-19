package com.g2t2.util;

import java.util.*;

import java.util.stream.*;

import com.g2t2.enums.CardColour;
import com.g2t2.types.*;

public class Display {

    // Display all stacks grouped by CardColour with two stacks per row
    public static void displayAllStacks(List<Card> cards) {
        // Sort cards by colour
        Collections.sort(cards);

        // Group cards by colour using streams
        Map<CardColour, List<Card>> groupedCards = cards.stream()
                .collect(Collectors.groupingBy(Card::getColour));

        // Convert each stack into a string representation
        List<String> stackLines = groupedCards.entrySet().stream()
                .map(entry -> getStackAsString(entry.getValue(), entry.getKey()))
                .collect(Collectors.toList());

        // // Print stacks two at a time
        // printStacksInPairs(stackLines);

        // Print stacks three at a time
        printStacksInGroups(stackLines);
        System.out.println(Constants.ANSI_RESET);

        System.out.println(Constants.DIVIDER);
    }

    // Get a single stack as a string representation (multi-line)
    private static String getStackAsString(List<Card> cards, CardColour currentCardColour) {
        StringBuilder stackBuilder = new StringBuilder();
        String ansiCodeString = currentCardColour.getAnsiCode();
        stackBuilder.append(ansiCodeString);

        int size = cards.size();

        stackBuilder.append(ansiCodeString + getCardStackLine1(size) + "\n");
        stackBuilder.append(ansiCodeString + getCardStackLine2(cards) + "\n");
        stackBuilder.append(ansiCodeString + getCardStackDefault(size) + "\n");
        stackBuilder.append(ansiCodeString + getCardStackLine4(size, currentCardColour) + "\n");
        stackBuilder.append(ansiCodeString + getCardStackDefault(size) + "\n");
        stackBuilder.append(ansiCodeString + getCardStackLine5(size, cards.get(cards.size() - 1).getValue()) + "\n");

        stackBuilder.append(Constants.ANSI_RESET);
        return stackBuilder.toString();
    }

    // // Print stacks in pairs (two per row)
    // private static void printStacksInPairs(List<String> stackLines) {
    // String[] emptyLines = new String[6]; // Each stack has 6 lines
    // Arrays.fill(emptyLines, ""); // Initialize empty lines for padding

    // for (int i = 0; i < stackLines.size(); i += 2) {
    // String[] firstStack = stackLines.get(i).split("\n");
    // String[] secondStack = (i + 1 < stackLines.size()) ? stackLines.get(i +
    // 1).split("\n") : emptyLines;

    // for (int j = 0; j < Math.min(firstStack.length, secondStack.length); j++) {
    // System.out.println(firstStack[j] + " " + secondStack[j]);
    // }
    // }
    // }

    // Print stacks in groups (three per row)
    private static void printStacksInGroups(List<String> stackLines) {
        String[] emptyLines = new String[6]; // Each stack has 6 lines
        Arrays.fill(emptyLines, ""); // Initialize empty lines for padding

        for (int i = 0; i < stackLines.size(); i += 3) { // Iterate three stacks at a time
            String[] firstStack = stackLines.get(i).split("\n");
            String[] secondStack = (i + 1 < stackLines.size()) ? stackLines.get(i + 1).split("\n") : emptyLines;
            String[] thirdStack = (i + 2 < stackLines.size()) ? stackLines.get(i + 2).split("\n") : emptyLines;

            for (int j = 0; j < Math.min(firstStack.length, Math.min(secondStack.length, thirdStack.length)); j++) {
                System.out.println(firstStack[j] + "   " + secondStack[j] + "   " + thirdStack[j]);
            }
        }
    }

    private static String getCardStackLine1(int size) {
        StringBuilder line = new StringBuilder(" ");
        for (int i = 0; i < size; i++) {
            line.append("___");
        }
        line.append("_____ ");
        return line.toString();
    }

    private static String getCardStackLine2(List<Card> cards) {
        StringBuilder line = new StringBuilder();
        for (Card card : cards) {
            line.append("|" + card.getValue());
            if (card.getValue() != 10) {
                line.append(" ");
            }
        }
        line.append("      |");
        return line.toString();
    }

    private static String getCardStackDefault(int size) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < size; i++) {
            line.append("|  ");
        }
        line.append("      |");
        return line.toString();
    }

    private static String getCardStackLine4(int size, CardColour currentCardColour) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < size; i++) {
            line.append("|  ");
        }
        line.append(" ").append(Constants.EMOJIS.get(currentCardColour.ordinal())).append("   |");
        return line.toString();
    }

    private static String getCardStackLine5(int size, int lastCardValue) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < size; i++) {
            line.append("|__");
        }
        line.append("____");
        if (lastCardValue == 10) {
            line.append(lastCardValue + "|");
        } else {
            line.append("_" + lastCardValue + "|");
        }
        return line.toString();
    }

    // ----------------------------------------------------------------
    // Display all cards side by side in a compact format
    public static void displayCardsSideBySide(List<Card> cards) {
        // Generate each card as a string representation
        List<String[]> cardLines = cards.stream()
                .map(Display::getCardAsStringArray)
                .toList();

        // Print the cards row by row (compact format)
        for (int i = 0; i < 6; i++) { // Each card has 5 lines
            for (String[] card : cardLines) {
                System.out.print(card[i] + "   "); // Add spacing between cards
            }
            System.out.println(Constants.ANSI_RESET); // Move to the next line after printing all cards in the row
        }
        System.out.println();
        for (int i = 0; i < cards.size(); i++) {
            System.out.printf("  「 %d 」    ", (i + 1));
        } // 「 ✦ 𝐍𝐚𝐦𝐞 ✦ 」
        System.out.println();
    }

    // Generate a single card as a string array (multi-line representation)
    private static String[] getCardAsStringArray(Card card) {
        String[] cardLines = new String[6];
        int value = card.getValue();
        String ansi = card.getColour().getAnsiCode();
        String emoji = Constants.EMOJIS.get(card.getColour().ordinal());

        cardLines[0] = ansi + " ________ ";
        cardLines[1] = ansi + "|" + value + "       |";
        if (value == 10) {
            cardLines[1] = ansi + "|" + value + "      |";
        }
        cardLines[2] = ansi + "|        |";
        cardLines[3] = ansi + "|   " + emoji + "   |";
        cardLines[4] = ansi + "|        |";
        cardLines[5] = ansi + "|_______" + value + "|";
        if (value == 10) {
            cardLines[5] = ansi + "|______" + value + "|";
        }
        return cardLines;
    }

    public static String colorString(String s) {
        return CardColour.BLUE.getAnsiCode() + s;
    }

    public static String errorColour(String s) {
        return CardColour.RED.getAnsiCode() + s + Constants.ANSI_RESET;
    }

    public static void sayGoodBye() {
        System.out.println(Constants.GOODBYE);

    }
}
