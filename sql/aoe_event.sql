CREATE TABLE IF NOT EXISTS aoe_event
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY ,
    match_id     BIGINT        NULL,
    start_time   BIGINT        NULL,
    reward_date  BIGINT        NULL,
    detail       JSON,
    state       INT DEFAULT 0 NOT NULL,
    lucky_number INT DEFAULT 0 NOT NULL
);

CREATE INDEX IF NOT EXISTS match_id
    ON aoe_event (match_id);

CREATE TABLE IF NOT EXISTS event_participants
(
    event_id     BIGINT        NOT NULL,
    user_id      BIGINT        NOT NULL,
    lucky_number BIGINT        NOT NULL,
    status       INT DEFAULT 0 NOT NULL,
    create_time  BIGINT        NOT NULL,
    PRIMARY KEY (event_id, user_id)
);