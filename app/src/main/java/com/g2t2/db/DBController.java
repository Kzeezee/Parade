package com.g2t2.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import com.g2t2.util.Constants;

public class DBController {

    public static void init() {
        try (Connection conn = DriverManager.getConnection(Constants.DB_CONNECTION)) {
            if (conn != null) {
                createTables(conn);

                // Create tables
                Statement st = conn.createStatement();
                st.execute(Query.CREATE_PROFILE_TABLE);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void createTables(Connection connection) throws SQLException {
        Statement st = connection.createStatement();
        st.execute(Query.CREATE_PROFILE_TABLE);
    }
}
