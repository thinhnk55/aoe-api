CREATE TABLE IF NOT EXISTS aoe_event
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY ,
    match_id BIGINT NOT NULL,
    start_time BIGINT NOT NULL,
    reward_date BIGINT NOT NULL DEFAULT 0,
    award INT NOT NULL,
    winning_number INT NOT NULL DEFAULT 0,
    max_number INT NOT NULL,
    detail TEXT NOT NULL DEFAULT '{}',
    state INT NOT NULL DEFAULT 0
);

CREATE INDEX IF NOT EXISTS match_id
    ON aoe_event (match_id);

CREATE TABLE IF NOT EXISTS aoe_event_participants
(
    event_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    lucky_number INT NOT NULL,
    state INT NOT NULL  DEFAULT 0,
    create_time BIGINT NOT NULL,
    phone VARCHAR(32) NOT NULL,
    PRIMARY KEY (event_id, user_id)
);