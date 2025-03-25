package com.g2t2.util.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.*;

import com.g2t2.types.PlayerProfile;
import com.g2t2.util.Constants;

public class DBController {
    public static List<PlayerProfile> getPlayerProfiles() {
        List<PlayerProfile> result = new ArrayList<>();
        try (Connection conn = getConnection(); Statement st = conn.createStatement();) {
            ResultSet rs = st.executeQuery(Query.GET_PLAYER_PROFILE);

            while (rs.next()) {
                // Process Player Profile
                result.add(new PlayerProfile(
                        rs.getLong("id"), rs.getString("name"), rs.getInt("wins")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Something went wrong while executing " + Query.GET_PLAYER_PROFILE);
        }
        // Return result
        return result;
    }

    public static boolean createPlayerProfile(String name) {
        final String SQL = "INSERT INTO profile VALUES(NULL,?,?)";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(SQL);) {
            ps.setString(1, name); // ID first, null due to auto-increment
            ps.setInt(2, 0); // Default to 0 wins
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(Constants.DB_CONNECTION);
    }
}
