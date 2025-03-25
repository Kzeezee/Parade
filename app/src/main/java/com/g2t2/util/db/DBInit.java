package com.g2t2.util.db;

import java.sql.*;

import com.g2t2.util.Constants;


public class DBInit {

    public static void init() {
        try (Connection conn = DriverManager.getConnection(Constants.DB_CONNECTION)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
                createTables(conn);

                // Create tables
                Statement st = conn.createStatement();
                st.execute(Query.CREATE_PROFILE_TABLE);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void createTables(Connection connection) throws SQLException {
        Statement st = connection.createStatement();
        st.execute(Query.CREATE_PROFILE_TABLE);
    }
}