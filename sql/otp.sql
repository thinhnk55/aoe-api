CREATE TABLE random_otp
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    random_otp INT UNIQUE NOT NULL,
    expired_time BIGINT NOT NULL ,
    status INT NOT NULL ,-- default = 0 and used = 1
    type tinyint
);