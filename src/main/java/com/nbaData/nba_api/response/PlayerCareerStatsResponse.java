package com.nbaData.nba_api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nbaData.nba_api.response.pojos.GeneralHigh;
import com.nbaData.nba_api.response.pojos.GeneralRanking;
import com.nbaData.nba_api.response.pojos.GeneralTotalsStats;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlayerCareerStatsResponse {

    private String resource;
    private Parameters parameters;

    @JsonProperty("resultSets")
    private List<ResultSet> resultSets;

    public PlayerCareerStatsResponse() {}


    // Classe interne pour Parameters
    @Getter
    @Setter
    public static class Parameters {
        @JsonProperty("PerMode")
        private String perMode;

        @JsonProperty("PlayerID")
        private Integer playerId;

        @JsonProperty("LeagueID")
        private String leagueId;

        public Parameters() {}

    }

    // Classe interne pour ResultSet
    @Getter
    @Setter
    public static class ResultSet {
        private String name;
        private List<String> headers;
        private List<List<Object>> rowSet;

        public ResultSet() {}

        private GeneralTotalsStats SeasonTotalsRegularSeason; // 27
        private GeneralTotalsStats CareerTotalsRegularSeason; //  24

        private GeneralTotalsStats SeasonTotalsPostSeason; // 27
        private GeneralTotalsStats CareerTotalsPostSeason; // 24

        private GeneralTotalsStats SeasonTotalsAllStarSeason; // 27
        private GeneralTotalsStats CareerTotalsAllStarSeason; // 24

        private GeneralTotalsStats SeasonTotalsCollegeSeason; // 27 bis
        private GeneralTotalsStats CareerTotalsCollegeSeason; // 24 bis

        private GeneralTotalsStats SeasonTotalsShowcaseSeason; //
        private GeneralTotalsStats CareerTotalsShowcaseSeason; //

        private GeneralRanking SeasonRankingsRegularSeason;
        private GeneralRanking SeasonRankingsPostSeason;

        private GeneralHigh SeasonHighs;
        private GeneralHigh CareerHighs;

    }

    public List<GeneralTotalsStats> getGeneralTotalsStats() {

        if (resultSets == null || resultSets.isEmpty()) {
            return List.of();
        }

        ResultSet PlayerCareerResultSet = resultSets.get(0);



        return null;
    }
}
