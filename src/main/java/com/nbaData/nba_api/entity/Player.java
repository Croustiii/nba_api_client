package com.nbaData.nba_api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
public class Player {

    @Id
    private Long personId;

    private String lastName;
    private String firstName;
    private String playerSlug;

    private Long teamId;
    private String teamAbbreviation;
    private String teamName;
    private String teamCity;

    private String jerseyNumber;
    private String position;
    private String height;
    private String weight;

    private String college;
    private String country;

    private Integer draftYear;
    private Integer draftRound;
    private Integer draftNumber;

    private Double rosterStatus;

    private Double pts;
    private Double reb;
    private Double ast;

    private String statsTimeframe;
    private Integer fromYear;
    private Integer toYear;
}
