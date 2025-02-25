package com.g2t2.util;

/**
 * Static only utility class housing useful misc. methods
 * @author Oliver
 */
public class Utility {
    /**
     * Mimics clearing the current console screen.
     * @author Oliver
     * @implSpec https://stackoverflow.com/questions/2979383/how-to-clear-the-console-using-java
     */
    public static void clearConsoleScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
