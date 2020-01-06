CREATE TABLE user_permissions
(
    user_id    BIGINT UNSIGNED NOT NULL,
    permission TEXT            NOT NULL,
    PRIMARY KEY (user_id, permission),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE logs_user_login_attempts
(
    id         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    user_id    BIGINT UNSIGNED NOT NULL,

    ip         VARBINARY(16)   NOT NULL,
    created_at TIMESTAMP       NOT NULL DEFAULT NOW(),

    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE logs_user_login_attempts
(
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,

    username    VARCHAR(30)     NOT NULL,
    ip          VARBINARY(16)   NOT NULL,
    fail_reason TEXT            NOT NULL,
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW(),

    PRIMARY KEY (id)
);