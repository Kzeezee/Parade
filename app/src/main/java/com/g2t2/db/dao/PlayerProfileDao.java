package com.g2t2.db.dao;

import java.util.List;

import com.g2t2.types.PlayerProfile;

public interface PlayerProfileDao {
    public List<PlayerProfile> getAllPlayerProfiles();
    public String createPlayerProfile(String name);
    public void updatePlayerProfile(PlayerProfile pp);
    public void deletePlayerProfile(PlayerProfile pp);
}
