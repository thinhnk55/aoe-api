CREATE TABLE telegram_user
(
    user_id BIGINT PRIMARY KEY  NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    telegram_id BIGINT,
    phone_number VARCHAR(20) UNIQUE,
    active_code VARCHAR(6) NOT NULL,
    telegram_info VARCHAR(2048) -- self formatted json String with related information provided by Telegram
);