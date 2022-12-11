package com.sdt.KingGame.model;

import com.sdt.KingGame.util.TurnInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@IdClass(GameTurnsPK.class)
@Table(name = "turns")
public class GameTurns implements Serializable {
    @Id
    @Column(name = "gameSessionId")
    private Long gameSessionId;

    @Id
    @Column(name = "gameNumber")
    private Integer gameNumber;

    @Id
    @Column(name = "circleNumber")
    private Integer circleNumber;

    @Id
    @Column(name = "playerId")
    private Integer playerId;

    @Column(name = "suit")
    private String suit;

    @Column(name = "magnitude")
    private Integer magnitude;

    public GameTurns() {
    }

    public GameTurns(Long gameSessionId, Integer gameNumber, Integer circleNumber, Integer playerId, String suit, Integer magnitude) {
        this.gameSessionId = gameSessionId;
        this.gameNumber = gameNumber;
        this.circleNumber = circleNumber;
        this.playerId = playerId;
        this.suit = suit;
        this.magnitude = magnitude;
    }

    public TurnInfo getTurnInfo() {
        return new TurnInfo(playerId, suit, magnitude);
    }
}
