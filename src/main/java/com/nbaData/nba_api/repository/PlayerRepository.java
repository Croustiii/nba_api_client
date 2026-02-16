package com.nbaData.nba_api.repository;

import com.nbaData.nba_api.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByTeamAbbreviation(String teamAbbreviation);

    List<Player> findByPosition(String position);

    List<Player> findByCountry(String country);

    List<Player> findByRosterStatus(Double rosterStatus);
}
