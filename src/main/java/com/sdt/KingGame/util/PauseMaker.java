package com.sdt.KingGame.util;

public class PauseMaker {
    private final Long pauseTime;
    private final Integer pausedBy;

    public PauseMaker(Long cancellationTime, Integer cancelledBy) {
        this.pauseTime = cancellationTime;
        this.pausedBy = cancelledBy;
    }

    public Long getPauseTime() {
        return pauseTime;
    }

    public Integer getPausedBy() {
        return pausedBy;
    }
}
