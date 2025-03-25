package com.g2t2.util.db;

public class Query {
    // Creation
    public static final String CREATE_PROFILE_TABLE = "CREATE TABLE IF NOT EXISTS profile ("
                + "	id INTEGER PRIMARY KEY,"
                + "	name text NOT NULL,"
                + "	wins integer"
                + ");";



    // Selection
    public static final String GET_PLAYER_PROFILE = "SELECT * FROM profile;";
}
