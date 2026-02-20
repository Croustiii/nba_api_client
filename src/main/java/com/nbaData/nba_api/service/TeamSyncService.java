package com.nbaData.nba_api.service;

import com.nbaData.nba_api.entity.Team;
import com.nbaData.nba_api.repository.TeamRepository;
import com.nbaData.nba_api.response.CommonTeamYearsResponse;
import com.nbaData.nba_api.response.pojos.TeamDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamSyncService {

    private static final Logger logger = LoggerFactory.getLogger(TeamSyncService.class);

    private final TeamRepository teamRepository;

    public TeamSyncService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    @Transactional
    public int syncTeams(CommonTeamYearsResponse response) {
        List<Team> teams = response.getTeams().stream()
                .map(this::toEntity)
                .collect(Collectors.toList());

        teamRepository.saveAll(teams);
        logger.info("{} équipes synchronisées en base", teams.size());
        return teams.size();
    }

    private Team toEntity(TeamDTO dto) {
        Team team = new Team();
        team.setTeamId(dto.getTeamId());
        team.setLeagueId(dto.getLeagueId());
        team.setAbbreviation(dto.getAbbreviation());
        team.setMinYear(dto.getMinYear());
        team.setMaxYear(dto.getMaxYear());
        return team;
    }
}
