package com.nbaData.nba_api.entity;

import java.io.Serializable;
import java.util.Objects;

public class PlayerCareerStatsId implements Serializable {

    private Long playerId;
    private String seasonId;

    public PlayerCareerStatsId() {}

    public PlayerCareerStatsId(Long playerId, String seasonId) {
        this.playerId = playerId;
        this.seasonId = seasonId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerCareerStatsId that = (PlayerCareerStatsId) o;
        return Objects.equals(playerId, that.playerId) && Objects.equals(seasonId, that.seasonId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, seasonId);
    }
}
