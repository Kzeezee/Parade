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
    public static final String WELCOMEMESSAGE_STRING = "Welcome to G2T2 Parade! Please select an option.";
    public static final String GAME_SELECTION_OPTION_ERROR_MSG = "Please choose a valid option! (1 for now)";
    public static final String DIVIDER = "==================================================";
    public static final String ANSI_RESET = "\033[0m";

    public static final String BANNER = String.format(CardColour.BLUE.getAnsiCode() +
                "           ███████████    █████████   ███████████     █████████   ██████████   ██████████\n" + //
                "          ░░███░░░░░███  ███░░░░░███ ░░███░░░░░███   ███░░░░░███ ░░███░░░░███ ░░███░░░░░█\n" + //
                "           ░███    ░███ ░███    ░███  ░███    ░███  ░███    ░███  ░███   ░░███ ░███  █ ░ \n" + //
                "           ░██████████  ░███████████  ░██████████   ░███████████  ░███    ░███ ░██████   \n" + //
                "           ░███░░░░░░   ░███░░░░░███  ░███░░░░░███  ░███░░░░░███  ░███    ░███ ░███░░█   \n" + //
                "           ░███         ░███    ░███  ░███    ░███  ░███    ░███  ░███    ███  ░███ ░   █\n" + //
                "           █████        █████   █████ █████   █████ █████   █████ ██████████   ██████████\n" + //
                "          ░░░░░        ░░░░░   ░░░░░ ░░░░░   ░░░░░ ░░░░░   ░░░░░ ░░░░░░░░░░   ░░░░░░░░░░     " + ANSI_RESET);
    
    public static final List<String> EMOJIS = List.of(
        "🌹", "💧", "🪷 ", "🪻 ", "🍀", "🍂"
    );

    // Constants
    public static final List<CardColour> CARD_COLOURS = List.of(
        CardColour.RED, CardColour.BLUE, CardColour.SEAFOAM, CardColour.PURPLE, 
        CardColour.GREEN, CardColour.ORANGE);
}
