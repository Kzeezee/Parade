package com.g2t2.util;

import java.util.*;

import com.g2t2.enums.*;
import com.g2t2.types.*;

/**
 * Static only class to declare constants.
 * @author Oliver
 */
public class Constants {
    // Strings
    public static final String ANSI_RESET = "\033[0m";
    public static final String WELCOMEMESSAGE_STRING = " ⚜ Welcome to G2T2 Parade! Please select an option. (1 or 3 for now)";
    public static final String GAME_SELECTION_OPTION_ERROR_MSG = " ❌ Please choose a valid option!";
    public static final String DIVIDER = "\033[90m" + 
                    "─────────────────────────────────────── ⚜ ──────────────────────────────────────────" + ANSI_RESET;

    public static final String BANNER = String.format(CardColour.BLUE.getAnsiCode() + 
                    "                              ┌──────── ⚜ ───────┐\n" + //
                    "                              │    P A R A D E   │\n" + //
                    "                              └──────── ⚜ ───────┘\n" + ANSI_RESET);

    // public static final String BANNER = String.format(CardColour.BLUE.getAnsiCode() +
    //             "                              ▐▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▌\n" + //
    //             "                              ▐  P A R A D E  ▌\n" + //
    //             "                              ▐▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▌\n" + ANSI_RESET);


    public static final String GOODBYE =   CardColour.BLUE.getAnsiCode() + 
              "\n                    ┌──────────────────── ⚜ ───────────────────────┐\n" + //
                "                    │         Thank you for playing PARADE.        │\n" + //
                "                    │    Have a nice day and see you again soon!   │\n" + //
                "                    └──────────────────── ⚜ ───────────────────────┘\n" + ANSI_RESET;
    public static final List<String> EMOJIS = List.of(
        "🌹", "💧", "☀️ ", "🪻 ", "🍀", "🕷️ "
    );

    // Constants
    public static final List<CardColour> CARD_COLOURS = List.of(
        CardColour.RED, CardColour.BLUE, CardColour.YELLOW, CardColour.PURPLE, 
        CardColour.GREEN, CardColour.BLACK);
}
