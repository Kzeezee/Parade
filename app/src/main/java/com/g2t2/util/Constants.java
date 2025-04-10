package com.g2t2.util;

import java.util.List;

import com.g2t2.enums.CardColour;

/**
 * Static only class to declare constants.
 * 
 * @author Oliver
 */
public class Constants {
    // Card Display
    public static final String RED_ANSI = "\033[38;2;244;113;116m";
    public static final String BLUE_ANSI = "\033[38;2;85;192;215m";
    public static final String YELLOW_ANSI = "\033[38;2;255;255;0m";
    public static final String PURPLE_ANSI = "\033[38;2;147;112;219m";
    public static final String GREEN_ANSI = "\033[38;2;85;205;121m";
    public static final String BLACK_ANSI = "\033[38;5;251m";
    public static final List<String> EMOJIS = List.of("🌹", "💧", "☀️ ", "🪻 ", "🍀", "🕷️ ");
    public static final List<CardColour> CARD_COLOURS = List.of(CardColour.RED, CardColour.BLUE, CardColour.YELLOW,
    CardColour.PURPLE, CardColour.GREEN, CardColour.BLACK);
    public static final int CARD_HEIGHT = 5;
    
    // Game Headers
    public static final String ANSI_RESET = "\033[0m";
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

    public static final String ONLINE_WAITING_LOBBY_BANNER = String.format(CardColour.BLUE.getAnsiCode() +
            "                              ┌─────────── ⚜ ──────────┐\n" + //
            "                              │  ONLINE WAITING LOBBY  │\n" + //
            "                              └─────────── ⚜ ──────────┘\n" + ANSI_RESET);

    public static final String LOCAL_WAITING_LOBBY_BANNER = String.format(CardColour.BLUE.getAnsiCode() +
            "                              ┌─────────── ⚜ ─────────┐\n" + //
            "                              │  LOCAL WAITING LOBBY  │\n" + //
            "                              └─────────── ⚜ ─────────┘\n" + ANSI_RESET);

    public static final String GOODBYE = BLUE_ANSI + "\n" +
            "                    ┌──────────────────── ⚜ ───────────────────────┐\n" + //
            "                    │         Thank you for playing PARADE.        │\n" + //
            "                    │    Have a nice day and see you again soon!   │\n" + //
            "                    └──────────────────── ⚜ ───────────────────────┘\n" + ANSI_RESET;

    // Commands
    public static final String CREATE_GAME = "CREATE GAME";
    public static final String JOIN_GAME = "JOIN GAME";
    public static final String ADD_CPU = "ADD CPU"; 
    public static final String ADD_PLAYER = "ADD PLAYER"; 
    public static final String REMOVE_CPU = "REMOVE CPU";
    public static final String REMOVE_PLAYER = "REMOVE PLAYER";
    public static final String START_GAME = "START GAME";
    public static final String LEAVE_GAME = "LEAVE GAME";
    public static final String ENTER_KEY = "";
    public static final String CANCEL_ACTION = "CANCEL ACTION";
    public static final String GO_BACK = "GO BACK";
    public static final String CLOSE_APP = "CLOSE APP";

    // Message To Indicate Things
    public static final String COMPLETE_TURN = "$complete_turn$";
    public static final String PUT_TO_COLLECTION = "$put_to_collection$";
    public static final String REFRESH_PAGE = "$refresh_page$";
    public static final String NEW_GAME = "$new_game$";
    public static final String GAME_ENDED = "$game_ended$";

    // Error Message Indicators
    public static final String INVALID_GAME_ID = "INVALID GAME ID";
    public static final String FULL_CAPACITY = "FULL CAPACITY";

    // Error Messages
    public static final String INVALID_OPTION_ERROR_MSG = Display.errorColour(" ❌ Please enter a number or an appropriate command");
    public static final String INVALID_COMMAND = Display.errorColour(" ❌ " + "INVALID COMMAND");

    // Prompts
    public static final String LEAVE_GAME_PROMPT =" 📌 To leave the game, enter '" + Display.colorString(Constants.LEAVE_GAME) + "'";
    public static final String CLOSE_APP_PROMPT = " 📌 To leave the application, enter: '" + Display.colorString(Constants.CLOSE_APP) + "'";
    public static final String GO_BACK_PROMPT = " 📌 To go back, enter: '" + Display.colorString(Constants.GO_BACK) + "'";
    public static final String CREATE_GAME_PROMPT = " 📌 To create a game, enter: '" + Display.colorString(Constants.CREATE_GAME) + "'";
    public static final String JOIN_GAME_PROMPT = " 📌 To join a game, enter: '" + Display.colorString(Constants.JOIN_GAME) + "'";
    public static final String START_GAME_PROMPT = " 📌 To start the game, enter '" + Display.colorString(Constants.START_GAME) + "'";
    public static final String REMOVE_CPU_PROMPT = " 📌 To remove CPU(s), enter '" + Display.colorString(Constants.REMOVE_CPU) + "'";
    public static final String ADD_CPU_PROMPT = " 📌 To add CPU(s), enter '" + Display.colorString(Constants.ADD_CPU) + "'";
    public static final String ADD_TO_PARADE_PROMPT = " 📌 Which card do you want to place in the Parade?";
    public static final String CANCEL_ACTION_PROMPT = " 📌 Enter '" + Display.colorString(Constants.CANCEL_ACTION) + "' to stop this action.";

    // Game Setting Numbers
    public static final int MIN_PLAYER_NO = 2;
    public static final int MAX_PLAYER_NO = 6;
    public static final int MIN_CPU_DIFFICULTY = 1;
    public static final int MAX_CPU_DIFFICULTY = 3;

    // Database
    public static final String DB_CONNECTION = "jdbc:sqlite:../parade.db";

    // Game Instructions
    public static final String GAME_INSTRUCTION_OBJ = Display.colorString("\n 🎯 Objective\n")
            + "End the game with the lowest score by avoiding collecting cards from the parade.\n" + DIVIDER;

    public static final String GAME_INSTRUCTION_COMPONENT = Display.colorString("\n 🃏 Components\n")
            + "66 cards in 6 colors (values 0-10)\n" + DIVIDER;

    public static final String GAME_INSTRUCTION_SETUP = Display.colorString("\n 🛠 Setup\n")
            + "1️⃣ Shuffle the deck.  \n"
            + "2️⃣ Deal 5 cards to each player.  \n"
            + "3️⃣ Place 6 cards face-up in a row (the parade 🎉).  \n"
            + "4️⃣ Mark the front of the parade with the game box 📦.  \n"
            + "5️⃣ Remaining cards = draw pile 🔄.  \n" + DIVIDER;

    public static final String GAME_INSTRUCTION_PLAY = Display.colorString("\n ▶️ Gameplay\n")
            + "Players take turns clockwise:\n"
            + "1️⃣ Play a Card: Place a card from your hand at the end of the parade ➡️.\n"
            + "2️⃣ Remove Cards (if applicable):\n"
            + "   • Count parade cards (excluding your played card).\n"
            + "   • If this count > your card's value:\n"
            + "   • Cards beyond your card's value enter \"removal mode.\" 🚨\n"
            + "   • Remove cards that:\n"
            + "      ⚬ Match your card's color 🎨, OR\n"
            + "      ⚬ Have a value ≤ your card's value 🔢.\n"
            + "   • Add removed cards to your collection 🃏.\n"
            + "3️⃣ Draw a Card: Replenish your hand to 5 cards 🔄.\n" + DIVIDER;

    public static final String GAME_INSTRUCTION_ENDGAME = Display.colorString("\n ⏳ Endgame\n")
            + "The game ends when:\n"
            + "1️⃣ A player collects cards of all 6 colors, OR\n"
            + "2️⃣ The draw pile is exhausted 📉.\n\n"
            + "Each player takes one final turn (no drawing afterward).\n" + DIVIDER;

    public static final String GAME_INSTRUCTION_SPECIAL_RULES = Display.colorString("\n ✨ Special Rules\n")
            + "Playing a card with value 0 triggers removal mode for all parade cards! 💥\n" + DIVIDER;

    public static final String GAME_INSTRUCTION_SCORING = Display.colorString("\n 🧮 Scoring\n")
            + "1️⃣ Choose 2 cards in hand to add to your collection.  \n"
            + "2️⃣ For each color:  \n"
            + "   • Majority owner flips their cards face down; each = 1 point 🔢.  \n"
            + "   • Others sum up face-up card values 💳.\n"
            + "   💡 For 2 player game, a player needs to have 2 cards or more to be considered majority owner\n"
            + "3️⃣ Total points; lowest score wins 🏆. In ties, fewer collected cards wins! 🎉\n" + DIVIDER;
}
