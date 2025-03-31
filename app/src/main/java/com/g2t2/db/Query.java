package com.g2t2.db;

public interface Query {
    // Player Profile
    public static final String CREATE_PROFILE_TABLE = "CREATE TABLE IF NOT EXISTS profile ("
                + "	id INTEGER PRIMARY KEY,"
                + " uuid text NOT NULL,"
                + "	name text NOT NULL,"
                + "	wins integer"
                + ");";
    public static final String GET_PLAYER_PROFILE = "SELECT * FROM profile;";
    public static final String CREATE_PLAYER_PROFILE = "INSERT INTO profile VALUES(NULL,?,?,?)";
    public static final String UPDATE_PLAYER_PROFILE = "UPDATE profile SET"
                + " name = ?,"
                + " wins = ?"
                + " WHERE uuid = ?";
    public static final String DELETE_PLAYER_PROFILE = "DELETE FROM profile WHERE id = ?";
}
