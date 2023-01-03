CREATE TABLE IF NOT EXISTS turns (
  game_session_id BIGINT NOT NULL,
   game_number INTEGER NOT NULL,
   circle_number INTEGER NOT NULL,
   player_id INTEGER NOT NULL,
   suit VARCHAR(255),
   magnitude INTEGER,
   CONSTRAINT pk_turns PRIMARY KEY (game_session_id, game_number, circle_number, player_id)
)