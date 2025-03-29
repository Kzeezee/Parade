package com.g2t2.game;

import com.g2t2.util.Display;

public class Prompt {
    public static final String GAME_DIFFICULTY_OPTIONS = " 📌 Choose the difficulty level for your CPU players\n" + //
                                                                " 📌 Easy (1)\n" + //
                                                                " 📌 Medium (2)\n" + //
                                                                " 📌 Difficult (3)\n" + //
                                                                Display.colorString(" ⚜ Enter option ➢ ");

    public static final String LOCAL_MODE_OPTIONS = " 📌 Please select an option.\n" 
                                                    + " 📌 Play with friends (1)\n"
                                                    + " 📌 Play with CPU (2)\n" 
                                                    + Display.colorString(" ⚜ Enter option ➢ ");

    public static final String PLAYER_NO_ERROR_MESSAGE = Display.errorColour(" ❌ A minimum of 2 players and a maximum of 6 players are allowed." 
                                                                    + "Please enter a number between 2 to 6.");
}
