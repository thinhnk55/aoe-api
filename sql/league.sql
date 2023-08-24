CREATE TABLE IF NOT EXISTS aoe_match  (
     id BIGINT PRIMARY KEY AUTO_INCREMENT NOT NULL,
     star_current INT NOT NULL default 0,
     star_default_online INT NOT NULL default 0,
     star_default_offline INT NOT NULL default 0,
     time_expired BIGINT,
     detail VARCHAR(4096) NOT NULL DEFAULT '{}',        #{"description":"32432","percent_for_gamer":"100%","percent_for_viewer":"100%","percent_for_organizers":"34%","result":[]}
     donate_benefit VARCHAR(4096) NOT NULL DEFAULT '[]',
     state INT,
     create_time BIGINT NOT NULL
);