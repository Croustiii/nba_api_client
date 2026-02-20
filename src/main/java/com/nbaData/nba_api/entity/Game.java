package com.nbaData.nba_api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "games")
@IdClass(GameId.class)
@Getter
@Setter
@NoArgsConstructor
public class Game {

    @Id
    private String gameId;

    @Id
    private Long teamId;

    private String seasonId;
    private String teamAbbreviation;
    private String teamName;
    private String gameDate;
    private String matchup;
    private String wl;

    private Integer min;

    private Integer fgm;
    private Integer fga;
    private Double fgPct;

    private Integer fg3m;
    private Integer fg3a;
    private Double fg3Pct;

    private Integer ftm;
    private Integer fta;
    private Double ftPct;

    private Integer oreb;
    private Integer dreb;
    private Integer reb;

    private Integer ast;
    private Integer stl;
    private Integer blk;
    private Integer tov;
    private Integer pf;
    private Integer pts;

    private Double plusMinus;
}
