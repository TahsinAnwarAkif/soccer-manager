USE soccer_manager;

CREATE TABLE IF NOT EXISTS user (
    id          BIGINT          NOT NULL AUTO_INCREMENT,
    username    VARCHAR(50)     NOT NULL,
    email 	    VARCHAR(255)    NOT NULL,
    password    VARCHAR(1024)   NOT NULL,
    enabled     BOOLEAN         NOT NULL DEFAULT 1,
    created     BIGINT          NOT NULL,
    updated     BIGINT          NOT NULL,
    version     INT             NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE      (username),
    UNIQUE      (email)
);

CREATE TABLE IF NOT EXISTS team (
    user_id     BIGINT                    NOT NULL,
    name        VARCHAR(255)              NOT NULL,
    country     VARCHAR(64)               NOT NULL,
    budget      BIGINT                    NOT NULL DEFAULT 5000000,
    value       BIGINT                    NOT NULL DEFAULT 20000000,
    created     BIGINT                    NOT NULL,
    updated     BIGINT                    NOT NULL,
    version     INT                       NOT NULL DEFAULT 0,
    PRIMARY KEY (user_id),
    FOREIGN KEY fk_team_user_id (user_id) REFERENCES user (id)
);

CREATE TABLE IF NOT EXISTS player (
    id              BIGINT                          NOT NULL AUTO_INCREMENT,
    team_id         BIGINT                          NOT NULL,
    first_name      VARCHAR(255)                    NOT NULL,
    last_name       VARCHAR(255)                    NOT NULL,
    type            VARCHAR(64)                     NOT NULL,
    country         VARCHAR(64)                     NOT NULL,
    age             INT                             NOT NULL,
    value           BIGINT                          NOT NULL DEFAULT 1000000,
    created         BIGINT                          NOT NULL,
    updated         BIGINT                          NOT NULL,
    version         INT                             NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    FOREIGN KEY fk_player_team_id (team_id)         REFERENCES team (user_id)
);

CREATE TABLE IF NOT EXISTS transfer (
    player_id                                       BIGINT  NOT NULL,
    value                                           BIGINT  NOT NULL,
    created         BIGINT                          NOT NULL,
    updated         BIGINT                          NOT NULL,
    version         INT                             NOT NULL   DEFAULT 0,
    PRIMARY KEY (player_id),
    FOREIGN KEY fk_transfer_player_id (player_id)   REFERENCES player (id)
);
