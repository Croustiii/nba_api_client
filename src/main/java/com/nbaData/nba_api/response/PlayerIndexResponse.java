package com.nbaData.nba_api.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlayerIndexResponse {

    private String resource;
    private Parameters parameters;

    @JsonProperty("resultSets")
    private List<ResultSet> resultSets;

    public PlayerIndexResponse() {}

    @Getter
    @Setter
    public static class Parameters {
        @JsonProperty("Historical")
        private Integer historical;

        @JsonProperty("LeagueID")
        private String leagueId;

        @JsonProperty("Season")
        private String season;

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
