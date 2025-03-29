package com.g2t2.types;

/*
 * A class to store the player profiles 
 */
public class PlayerProfile {
    private long ID;
    private String uuid;
    private String name;
    private int wins;

    public PlayerProfile(long ID, String uuid, String name, int wins) {
        this.ID = ID;
        this.uuid = uuid;
        this.name = name;
        this.wins = wins;
    }

    public long getID() {
        return this.ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWins() {
        return wins;
    }
    
    public void setWins(int wins) {
        this.wins = wins;
    }

    
}
