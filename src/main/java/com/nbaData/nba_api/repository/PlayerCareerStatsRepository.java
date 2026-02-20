package com.nbaData.nba_api.repository;

import com.nbaData.nba_api.entity.PlayerCareerStats;
import com.nbaData.nba_api.entity.PlayerCareerStatsId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerCareerStatsRepository extends JpaRepository<PlayerCareerStats, PlayerCareerStatsId> {

    List<PlayerCareerStats> findByPlayerId(Long playerId);
}
