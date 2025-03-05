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

    // Constants
    public static final List<CardColour> cardColours = List.of(
        CardColour.RED, CardColour.BLUE, CardColour.PURPLE, 
        CardColour.GREEN, CardColour.GREY, CardColour.ORANGE);
}
