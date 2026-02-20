package com.nbaData.nba_api.entity;

import java.io.Serializable;
import java.util.Objects;

public class GameId implements Serializable {

    private String gameId;
    private Long teamId;

    public GameId() {}

    public GameId(String gameId, Long teamId) {
        this.gameId = gameId;
        this.teamId = teamId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameId that = (GameId) o;
        return Objects.equals(gameId, that.gameId) && Objects.equals(teamId, that.teamId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameId, teamId);
    }
}
