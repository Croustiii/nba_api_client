package com.nbaData.nba_api.response.pojos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Team {
// Classe Team pour représenter une équipe de manière plus lisible

    private String leagueId;
    private Long teamId;
    private String minYear;
    private String maxYear;
    private String abbreviation;

    public Team() {}

    public Team(String leagueId, Long teamId, String minYear, String maxYear, String abbreviation) {
        this.leagueId = leagueId;
        this.teamId = teamId;
        this.minYear = minYear;
        this.maxYear = maxYear;
        this.abbreviation = abbreviation;
    }

    @Override
    public String toString() {
        return "Team{" +
                "teamId=" + teamId +
                ", abbreviation='" + abbreviation + '\'' +
                ", years=" + minYear + "-" + maxYear +
                '}';
    }
}
