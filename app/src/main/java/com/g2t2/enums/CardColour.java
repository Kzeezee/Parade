package com.g2t2.enums;

import com.g2t2.util.Constants;

/**
 * Enum representing the colors of Parade cards, each associated with an ANSI color code.
 * The colors include red, blue, yellow, purple, green, and black.
 * These ANSI color codes are used for text styling in the console.
 * 
 * @author Oliver
 */
public enum CardColour {
    RED(Constants.RED_ANSI),
    BLUE(Constants.BLUE_ANSI),
    YELLOW(Constants.YELLOW_ANSI),
    PURPLE(Constants.PURPLE_ANSI),
    GREEN(Constants.GREEN_ANSI),
    BLACK(Constants.BLACK_ANSI);

    private final String ansiCode; // The ANSI color code associated with the card color.

    /**
     * Constructor to initialize the enum constant with its respective ANSI color code.
     * 
     * @param ansiCode The ANSI color code for the card color.
     */
    CardColour(String ansiCode) {
        this.ansiCode = ansiCode;
    }

    /**
     * Retrieves the ANSI color code associated with the card color.
     * 
     * @return The ANSI color code as a {@code String}.
     */
    public String getAnsiCode() {
        return ansiCode;
    }
}
