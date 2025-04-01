package com.g2t2.util;

import java.util.List;

import com.g2t2.enums.CardColour;

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
    public static final String INVALID_OPTION_ERROR_MSG = " ❌ Please choose a valid option!";
    public static final String WELCOMEMESSAGE_STRING = " ⚜ Welcome to G2T2 Parade! Please select an option. (1 or 3 for now)";
    public static final String DIVIDER = "\033[90m" + "───────────────────────────────────────" 
                                         + " ⚜ ──────────────────────────────────────────" + ANSI_RESET;

    public static final String BANNER = String.format(CardColour.BLUE.getAnsiCode()
                                        + "                              ┌──────── ⚜ ───────┐\n"  
                                        + "                              │    P A R A D E   │\n"
                                        + "                              └──────── ⚜ ───────┘\n" + ANSI_RESET);

    public static final String ONLINE_LOBBY_BANNER = String.format(CardColour.BLUE.getAnsiCode() +
        "                              ┌──────── ⚜ ───────┐\n" + //
        "                              │   ONLINE  LOBBY  │\n" + //
        "                              └──────── ⚜ ───────┘\n" + ANSI_RESET);

    public static final String WAITING_LOBBY_BANNER = String.format(CardColour.BLUE.getAnsiCode() +
        "                              ┌──────── ⚜ ───────┐\n" + //
        "                              │  WAITING  LOBBY  │\n" + //
        "                              └──────── ⚜ ───────┘\n" + ANSI_RESET);

    public static final String GOODBYE = BLUE_ANSI + "\n" +
            "                    ┌──────────────────── ⚜ ───────────────────────┐\n" + //
            "                    │         Thank you for playing PARADE.        │\n" + //
            "                    │    Have a nice day and see you again soon!   │\n" + //
            "                    └──────────────────── ⚜ ───────────────────────┘\n" + ANSI_RESET;

    public static final List<String> EMOJIS = List.of("🌹", "💧", "☀️ ", "🪻 ", "🍀", "🕷️ ");

    public static final String PROMPT_ADD_TO_PARADE = Display.colorString(" 📌 Which card do you want to place in the Parade?");

    // Commands
    public static final String CREATE_GAME = "c"; //"CREATE_GAME";
    public static final String JOIN_GAME = "j"; //"JOIN_GAME";
    public static final String ADD_CPU = "x"; //"ADD_CPU";
    public static final String START_GAME = "w"; //"START_GAME";
    public static final String LEAVE_GAME = "y"; //"LEAVE_GAME";
    public static final String ENTER_KEY = "";

    // Message To Indicate Things
    public static final String COMPLETE_TURN = "$complete_turn$";
    public static final String PUT_TO_COLLECTION = "$put_to_collection$";
    public static final String REFRESH_PAGE = "$refresh_page$";
    public static final String NEW_GAME = "$new_game$";

    // ERRORS
    public static final String INVALID_GAME_ID = "INVALID GAME ID";
    public static final String INVALID_COMMAND = "INVALID COMMAND";
    public static final String FULL_CAPACITY = "FULL CAPACITY";

    // Constants
    public static final List<CardColour> CARD_COLOURS = List.of(CardColour.RED, CardColour.BLUE, CardColour.YELLOW, 
                                                                CardColour.PURPLE, CardColour.GREEN, CardColour.BLACK);
    
    public static final String DB_CONNECTION = "jdbc:sqlite:../parade.db";

    public static final int MIN_PLAYER_NO = 2;
    public static final int MAX_PLAYER_NO = 6;

    public static final int MIN_CPU_DIFFICULTY = 1;
    public static final int MAX_CPU_DIFFICULTY = 3;
}
