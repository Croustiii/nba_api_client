package com.nbaData.nba_api.repository;

import com.nbaData.nba_api.entity.Game;
import com.nbaData.nba_api.entity.GameId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, GameId> {

    List<Game> findByGameId(String gameId);

    List<Game> findByTeamId(Long teamId);
}
