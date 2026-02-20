package com.nbaData.nba_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "player_career_stats")
@IdClass(PlayerCareerStatsId.class)
@Getter
@Setter
@NoArgsConstructor
public class PlayerCareerStats {

    @Id
    private Long playerId;

    @Id
    private String seasonId;

    private String leagueId;
    private Long teamId;
    private String teamAbbreviation;
    private Double playerAge;

    private Integer gp;
    private Integer gs;
    private Double min;

    private Double fgm;
    private Double fga;
    private Double fgPct;

    private Double fg3m;
    private Double fg3a;
    private Double fg3Pct;

    private Double ftm;
    private Double fta;
    private Double ftPct;

    private Double oreb;
    private Double dreb;
    private Double reb;

    private Double ast;
    private Double stl;
    private Double blk;
    private Double tov;
    private Double pf;
    private Double pts;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playerId", insertable = false, updatable = false)
    private Player player;
}
