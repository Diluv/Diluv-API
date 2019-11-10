# This will be updated up until production
CREATE TABLE users
(
    id            BIGINT AUTO_INCREMENT,
    username      VARCHAR(30)  NOT NULL UNIQUE,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password      CHAR(60)     NOT NULL,
    password_type VARCHAR(30)  NOT NULL,

    mfa           BOOL DEFAULT FALSE,
    mfa_secret    VARCHAR(16)  NOT NULL,

    avatar_url    VARCHAR(255) NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE user_mfa_recovery
(
    user_id  BIGINT       NOT NULL,

    2fa_code VARCHAR(255) NOT NULL,
    valid    BOOL DEFAULT TRUE,

    PRIMARY KEY (user_id, 2fa_code),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE game
(
    slug VARCHAR(200) NOT NULL,

    name VARCHAR(255) NOT NULL,
    url  VARCHAR(255) NOT NULL,

    PRIMARY KEY (slug)
);

CREATE TABLE game_modloader
(
    id        BIGINT AUTO_INCREMENT,

    game_slug VARCHAR(200) NOT NULL,
    name      VARCHAR(255) NOT NULL,

    url       VARCHAR(255) NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (game_slug) REFERENCES game (slug)
);

CREATE TABLE game_version
(
    id        BIGINT AUTO_INCREMENT,

    game_slug VARCHAR(200) NOT NULL,
    version   VARCHAR(255) NOT NULL,

    url       VARCHAR(255) NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (game_slug) REFERENCES game (slug)
);

# Project
CREATE TABLE project
(
    id              BIGINT AUTO_INCREMENT,
    name            VARCHAR(255)    NOT NULL,
    slug            VARCHAR(255)    NOT NULL,
    summary         VARCHAR(255)    NOT NULL,
    description     VARCHAR(255)    NOT NULL,
    logo_url        VARCHAR(255)    NOT NULL,
    cache_downloads BIGINT UNSIGNED NOT NULL,

    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW() ON UPDATE NOW(),

    owner_id        BIGINT          NOT NULL,
    game_slug       VARCHAR(200)    NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (owner_id) REFERENCES users (id),
    FOREIGN KEY (game_slug) REFERENCES game (slug)
);

CREATE TABLE project_author
(
    id            BIGINT AUTO_INCREMENT,

    project_id    BIGINT       NOT NULL,
    author_id     BIGINT       NOT NULL,

    role          VARCHAR(255) NOT NULL,

    uploaded_file BOOL DEFAULT FALSE,
    PRIMARY KEY (id),
    FOREIGN KEY (project_id) REFERENCES project (id),
    FOREIGN KEY (author_id) REFERENCES users (id)
);

CREATE TABLE project_author_permission
(
    project_author_id BIGINT       NOT NULL,
    permission        VARCHAR(255) NOT NULL,

    PRIMARY KEY (project_author_id, permission),
    FOREIGN KEY (project_author_id) REFERENCES project_author (id)
);

CREATE TABLE project_links
(
    project_id BIGINT       NOT NULL,
    type       VARCHAR(255) NOT NULL,
    url        VARCHAR(255) NOT NULL,

    PRIMARY KEY (project_id, type),
    FOREIGN KEY (project_id) REFERENCES project (id)
);

CREATE TABLE project_file
(
    id         BIGINT AUTO_INCREMENT,

    sha512     VARCHAR(255)    NOT NULL,
    crc32      VARCHAR(255)    NOT NULL,
    size       BIGINT UNSIGNED NOT NULL,

    changelog  VARCHAR(255)    NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW() ON UPDATE NOW(),

    reviewed   BOOL      DEFAULT FALSE,
    released   BOOL      DEFAULT FALSE,

    project_id BIGINT          NOT NULL,
    user_id    BIGINT          NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (project_id) REFERENCES project (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE project_file_version
(
    project_file_id BIGINT NOT NULL,
    game_version_id BIGINT NOT NULL,

    PRIMARY KEY (project_file_id, game_version_id),
    FOREIGN KEY (project_file_id) REFERENCES project_file (id),
    FOREIGN KEY (game_version_id) REFERENCES game_version (id)
);

CREATE TABLE project_file_modloader
(
    project_file_id BIGINT NOT NULL,
    modloader_id    BIGINT NOT NULL,

    PRIMARY KEY (project_file_id, modloader_id),
    FOREIGN KEY (project_file_id) REFERENCES project_file (id),
    FOREIGN KEY (modloader_id) REFERENCES game_modloader (id)
);

# Project File Queue
CREATE TABLE project_file_queue
(
    project_file_id BIGINT       NOT NULL,
    process_name    VARCHAR(255) NOT NULL,

    PRIMARY KEY (project_file_id, process_name),
    FOREIGN KEY (project_file_id) REFERENCES project_file (id)
);

CREATE TABLE nodecraft
(
    id         VARCHAR(36) NOT NULL,

    created_at TIMESTAMP DEFAULT NOW(),
    valid      BOOL      DEFAULT TRUE,

    PRIMARY KEY (id)
);