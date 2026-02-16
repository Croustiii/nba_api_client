package com.nbaData.nba_api.response.pojos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneralTotalsStats {

    private String PLAYER_ID;
    private String SEASON_ID;
    private String LEAGUE_ID;
    private String TEAM_ID;
    private String ORGANIZATION_ID;
    private String SCHOOL_NAME;

    private String TEAM_ABBREVIATION;
    private String PLAYER_AGE;
    private String GP;
    private String GS;
    private String MIN;
    private String FGM;
    private String FGA;
    private String FG_PCT;
    private String FG3M;
    private String FG3A;
    private String FG3_PCT;
    private String FTM;
    private String FTA;
    private String FT_PCT;
    private String OREB;
    private String DREB;
    private String REB;
    private String AST;
    private String STL;
    private String BLK;
    private String TOV;
    private String PF;
    private String PTS;

    public GeneralTotalsStats() {

    }
}
