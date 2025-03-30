package com.g2t2.types;

/*
 * A class to store the player profiles 
 */
public class PlayerProfile {
    private long ID;
    private String name;
    private int wins;

    /**
     * Creates a new PlayerProfile with the specified ID, name, and number of wins.
     * 
     * @param ID    ID of the PlayerProfile
     * @param name  name of the PlayerProfile
     * @param wins  number of wins of the PlayerProfile
     */
    public PlayerProfile(long ID, String name, int wins) {
        this.ID = ID;
        this.name = name;
        this.wins = wins;
    }

    /**
     * Returns the PlayerProfile's ID as an integer.
     * 
     * @return the PlayerProfile's ID
     */ 
    public long getID() {
        return this.ID;
    }

    /**
     * Sets the PlayerProfiles's ID to the specified ID.
     * 
     * @param ID the ID to be set to
     */
    public void setID(long ID) {
        this.ID = ID;
    }
    
    /**
     * Returns the PlayerProfile's name as a String.
     * 
     * @return the PlayerProfile's name
     */ 
    public String getName() {
        return name;
    }

    /**
     * Sets the PlayerProfiles's name to the specified name.
     * 
     * @param name the name to be set to
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the PlayerProfile's number of wins as an integer.
     * 
     * @return the PlayerProfile's number of wins
     */ 
    public int getWins() {
        return wins;
    }

    /**
     * Sets the PlayerProfiles's number of wins to the specified wins.
     * 
     * @param wins the number of wins to be set to
     */
    public void setWins(int wins) {
        this.wins = wins;
    }
}
