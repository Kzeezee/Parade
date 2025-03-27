package com.g2t2.enums;

import com.g2t2.util.Constants;

/**
 * Enum determing the respective Parade cards' colours (red, blue, purple, green, grey, orange)
 * with ansi colour codes
 * @author Oliver
 */
public enum CardColour {

    RED(Constants.RED_ANSI),
    BLUE(Constants.BLUE_ANSI),
    YELLOW(Constants.YELLOW_ANSI),
    PURPLE(Constants.PURPLE_ANSI),
    GREEN(Constants.GREEN_ANSI),
    BLACK(Constants.BLACK_ANSI);

    private final String ansiCode;

    // Constructor
    CardColour(String ansiCode) {
        this.ansiCode = ansiCode;
    }

    // Getter
    public String getAnsiCode() {
        return ansiCode;
    }
}
