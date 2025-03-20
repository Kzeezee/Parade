package com.g2t2.util;

import java.util.*;

import com.g2t2.enums.*;
import com.g2t2.types.*;

/**
 * Static only class to declare constants.
 * 
 * @author Oliver
 */
public class Constants {
    // Strings
    public static final String RED_ANSI = "\033[38;2;244;113;116m";
    public static final String BLUE_ANSI = "\033[38;2;85;192;215m";
    public static final String YELLOW_ANSI = "\033[38;2;255;255;0m";
    public static final String PURPLE_ANSI = "\033[38;2;147;112;219m";
    public static final String GREEN_ANSI = "\033[38;2;85;205;121m";
    public static final String BLACK_ANSI = "\033[38;2;1;1;1m";
    public static final String ANSI_RESET = "\033[0m";

    public static final String WELCOMEMESSAGE_STRING = " ⚜ Welcome to G2T2 Parade! Please select an option. (1 or 3 for now)";
    public static final String GAME_SELECTION_OPTION_ERROR_MSG = " ❌ Please choose a valid option!";
    public static final String DIVIDER = "\033[90m" +
            "─────────────────────────────────────── ⚜ ──────────────────────────────────────────" + ANSI_RESET;

    public static final String BANNER = String.format(CardColour.BLUE.getAnsiCode() +
            "                              ┌──────── ⚜ ───────┐\n" + //
            "                              │    P A R A D E   │\n" + //
            "                              └──────── ⚜ ───────┘\n" + ANSI_RESET);

    // public static final String BANNER =
    // String.format(CardColour.BLUE.getAnsiCode() +
    // " ▐▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▌\n" + //
    // " ▐  P A R A D E  ▌\n" + //
    // " ▐▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▌\n" + ANSI_RESET);

    public static final String GOODBYE = BLUE_ANSI + "\n" +
            "                    ┌──────────────────── ⚜ ───────────────────────┐\n" + //
            "                    │         Thank you for playing PARADE.        │\n" + //
            "                    │    Have a nice day and see you again soon!   │\n" + //
            "                    └──────────────────── ⚜ ───────────────────────┘\n" + ANSI_RESET;
    public static final List<String> EMOJIS = List.of(
            "🌹", "💧", "☀️ ", "🪻 ", "🍀", "🕷️ ");

    // Constants
    public static final List<CardColour> CARD_COLOURS = List.of(
            CardColour.RED, CardColour.BLUE, CardColour.YELLOW, CardColour.PURPLE,
            CardColour.GREEN, CardColour.BLACK);

}
