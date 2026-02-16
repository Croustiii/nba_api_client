package com.nbaData.nba_api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nbaData.nba_api.response.pojos.Team;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

// Classe principale pour la réponse complète
@Getter
@Setter
public class CommonTeamYearsResponse {

    private String resource;
    private Parameters parameters;

    @JsonProperty("resultSets")
    private List<ResultSet> resultSets;

    public CommonTeamYearsResponse() {}


    // Classe interne pour Parameters
    public static class Parameters {
        @JsonProperty("LeagueID")
        private String leagueId;

        public Parameters() {}

        public String getLeagueId() {
            return leagueId;
        }

        public void setLeagueId(String leagueId) {
            this.leagueId = leagueId;
        }
    }

    // Classe interne pour ResultSet
    @Getter
    @Setter
    public static class ResultSet {
        private String name;
        private List<String> headers;
        private List<List<Object>> rowSet;

        public ResultSet() {}

    }

    public List<Team> getTeams() {
        if (resultSets == null || resultSets.isEmpty()) {
            return List.of();
        }

        ResultSet teamYearsResultSet = resultSets.get(0);
        List<Team> teams = new ArrayList<>();

        for (List<Object> row : teamYearsResultSet.getRowSet()) {
            Team team = new Team();
            team.setLeagueId((String) row.get(0));
            team.setTeamId(((Number) row.get(1)).longValue());
            team.setMinYear((String) row.get(2));
            team.setMaxYear((String) row.get(3));
            team.setAbbreviation((String) row.get(4));
            teams.add(team);
        }

        return teams;
    }
}
