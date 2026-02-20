package com.nbaData.nba_api.response.pojos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamDTO {
// Classe TeamDTO pour représenter une équipe de manière plus lisible

    private String leagueId;
    private Long teamId;
    private String minYear;
    private String maxYear;
    private String abbreviation;

    public TeamDTO() {}

    public TeamDTO(String leagueId, Long teamId, String minYear, String maxYear, String abbreviation) {
        this.leagueId = leagueId;
        this.teamId = teamId;
        this.minYear = minYear;
        this.maxYear = maxYear;
        this.abbreviation = abbreviation;
    }

    @Override
    public String toString() {
        return "TeamDTO{" +
                "teamId=" + teamId +
                ", abbreviation='" + abbreviation + '\'' +
                ", years=" + minYear + "-" + maxYear +
                '}';
    }
}
