package com.g2t2.db.dao.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.g2t2.db.dao.PlayerProfileDao;
import com.g2t2.db.Query;
import com.g2t2.types.PlayerProfile;
import com.g2t2.util.Constants;

/**
 * This class provides concrete implementations of the database operations defined
 * in the PlayerProfileDao interface.
 */
public class PlayerProfileDaoImpl implements PlayerProfileDao {
    /**
     * Retrieves all player profiles from the database.
     * 
     * @return a list of all PlayerProfile objects stored in the database
     * @throws RuntimeException if there is an error executing the database query
     */
    public List<PlayerProfile> getAllPlayerProfiles() {
        List<PlayerProfile> result = new ArrayList<>();
        try (Connection conn = getConnection(); Statement st = conn.createStatement();) {
            ResultSet rs = st.executeQuery(Query.GET_PLAYER_PROFILE);

            while (rs.next()) {
                // Process Player Profile
                result.add(new PlayerProfile(
                        rs.getLong("id"), rs.getString("uuid"), rs.getString("name"), rs.getInt("wins")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Something went wrong while executing " + Query.GET_PLAYER_PROFILE);
        }
        // Return result
        return result;
    }

    /**
     * Creates a new player profile in the database.
     * 
     * This method generates a unique UUID for the new profile and inserts it into
     * the database with the specified name and an initial win count of 0.
     * 
     * @param name the name for the new player profile
     * @return the UUID of the newly created profile, or null if creation fails
     */
    public String createPlayerProfile(String name) {
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(Query.CREATE_PLAYER_PROFILE);) {
            String uuid = UUID.randomUUID().toString();
            ps.setString(1, uuid);
            ps.setString(2, name);
            ps.setInt(3, 0); // Default to 0 wins
            ps.executeUpdate();
            return uuid;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates an existing player profile in the database.
     * 
     * This method updates the name and win count of an existing player profile.
     * 
     * @param pp the PlayerProfile object containing the updated information
     */
    public void updatePlayerProfile(PlayerProfile pp) {
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(Query.UPDATE_PLAYER_PROFILE);) {
            ps.setString(3, pp.getUuid());
            ps.setString(1, pp.getName());
            ps.setInt(2, pp.getWins());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a player profile from the database.
     * 
     * @param pp the PlayerProfile object to be deleted
     */
    public void deletePlayerProfile(PlayerProfile pp) {
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(Query.DELETE_PLAYER_PROFILE);) {
            ps.setLong(1, pp.getID());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates and returns a new database connection
     * 
     * @return a Connection object representing the database connection
     * @throws SQLException if a database access error occurs
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(Constants.DB_CONNECTION);
    }
}
