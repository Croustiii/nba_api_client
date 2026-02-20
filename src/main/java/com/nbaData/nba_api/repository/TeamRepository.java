package com.nbaData.nba_api.repository;

import com.nbaData.nba_api.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findByAbbreviation(String abbreviation);

    List<Team> findByLeagueId(String leagueId);
}
