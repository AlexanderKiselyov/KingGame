package com.sdt.KingGame.repository;

import com.sdt.KingGame.model.GameTurns;
import com.sdt.KingGame.model.GameTurnsPK;
import org.springframework.data.repository.CrudRepository;

public interface TurnRepository extends CrudRepository<GameTurns, GameTurnsPK> {
}
