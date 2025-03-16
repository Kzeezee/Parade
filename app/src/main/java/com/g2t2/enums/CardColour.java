package com.g2t2.enums;

/**
 * Enum determing the respective Parade cards' colours (red, blue, purple, green, grey, orange)
 * with ansi colour codes
 * @author Oliver
 */
public enum CardColour {
    // RED("\033[0;31m"),
    // BLUE("\033[38;2;85;192;215m"), // 34m
    // PURPLE("\033[35m"),
    // GREEN("\033[38;2;85;205;121m"),
    // GREY("\033[90m"),
    // ORANGE("\033[38;5;208m");

    RED("\033[38;2;244;113;116m"),
    BLUE("\033[38;2;85;192;215m"),
    SEAFOAM("\033[38;2;152;251;152m"),
    PURPLE("\033[38;2;147;112;219m"),
    GREEN("\033[38;2;85;205;121m"),
    ORANGE("\033[38;2;255;229;180m");

    private final String ansiCode;

    // Constructor
    CardColour (String ansiCode) {
        this.ansiCode = ansiCode;
    }

    // Getter
    public String getAnsiCode() {
        return ansiCode;
    }
}
