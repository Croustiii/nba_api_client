package com.nbaData.nba_api.response.pojos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneralHigh {

    private String PLAYER_ID;
    private String GAME_ID;
    private String GAME_DATE;
    private String VS_TEAM_ID;
    private String VS_TEAM_CITY;
    private String VS_TEAM_NAME;
    private String VS_TEAM_ABBREVIATION;
    private String STAT;
    private String STAT_VALUE;
    private String STAT_ORDER;
    private String DATE_EST;

}
