package com.g2t2.enums;

import com.g2t2.util.Constants;

/**
 * Enum determing the respective Parade cards' colours (red, blue, purple, green, grey, orange)
 * with ansi colour codes
 * @author Oliver
 */
public enum CardColour {
    // RED("\033[38;2;244;113;116m"),
    // BLUE("\033[38;2;85;192;215m"),
    // // SEAFOAM("\033[38;2;152;251;152m"),
    // YELLOW("\033[38;2;255;255;0m"),
    // PURPLE("\033[38;2;147;112;219m"),
    // GREEN("\033[38;2;85;205;121m"),
    // BLACK("\033[38;2;1;1;1m");
    // // ORANGE("\033[38;2;255;229;180m");

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
