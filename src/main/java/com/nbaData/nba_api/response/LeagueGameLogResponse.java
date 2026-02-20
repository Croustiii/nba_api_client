package com.nbaData.nba_api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LeagueGameLogResponse {

    private String resource;
    private Parameters parameters;

    @JsonProperty("resultSets")
    private List<ResultSet> resultSets;

    public LeagueGameLogResponse() {}

    @Getter
    @Setter
    public static class Parameters {
        @JsonProperty("Season")
        private String season;

        @JsonProperty("SeasonType")
        private String seasonType;

        @JsonProperty("LeagueID")
        private String leagueId;

        @JsonProperty("PlayerOrTeam")
        private String playerOrTeam;

        @JsonProperty("Direction")
        private String direction;

        @JsonProperty("Sorter")
        private String sorter;

        @JsonProperty("Counter")
        private Integer counter;

        public Parameters() {}
    }

    @Getter
    @Setter
    public static class ResultSet {
        private String name;
        private List<String> headers;
        private List<List<Object>> rowSet;

        public ResultSet() {}
    }
}
