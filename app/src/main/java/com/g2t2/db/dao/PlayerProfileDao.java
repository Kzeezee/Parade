package com.g2t2.db.dao;

import java.util.List;

import com.g2t2.types.PlayerProfile;

/**
 * DAO interface for PlayerProfile operations.
 * 
 * This interface defines the standard operations to be performed on PlayerProfile objects
 * in the database. It provides methods for retrieving, creating, updating, and deleting
 * player profiles.
 */
public interface PlayerProfileDao {
    /**
     * Retrieves all player profiles from the database.
     * 
     * @return a list of all PlayerProfile objects stored in the database
     */
    public List<PlayerProfile> getAllPlayerProfiles();
    
    /**
     * Creates a new player profile with the specified name.
     * 
     * @param name the name for the new player profile
     * @return a unique identifier for the newly created profile
     */
    public String createPlayerProfile(String name);
    
    /**
     * Updates an existing player profile in the database.
     * 
     * @param pp the PlayerProfile object containing updated information
     */
    public void updatePlayerProfile(PlayerProfile pp);
    
    /**
     * Removes a player profile from the database.
     * 
     * @param pp the PlayerProfile object to be deleted
     */
    public void deletePlayerProfile(PlayerProfile pp);
}
