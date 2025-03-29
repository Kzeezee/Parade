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

public class PlayerProfileDaoImpl implements PlayerProfileDao {
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

    public String createPlayerProfile(String name) {
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(Query.CREATE_PLAYER_PROFILE);) {
            String uuid = UUID.randomUUID().toString();
            ps.setString(1, uuid);
            ps.setString(2, name); // ID first, null due to auto-increment
            ps.setInt(3, 0); // Default to 0 wins
            ps.executeUpdate();
            return uuid;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updatePlayerProfile(PlayerProfile pp) {
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(Query.UPDATE_PLAYER_PROFILE);) {
            ps.setLong(3, pp.getID());
            ps.setString(1, pp.getName()); // ID first, null due to auto-increment
            ps.setInt(2, pp.getWins()); // Default to 0 wins
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePlayerProfile(PlayerProfile pp) {
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(Query.DELETE_PLAYER_PROFILE);) {
            ps.setLong(1, pp.getID());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(Constants.DB_CONNECTION);
    }
}
