DROP TABLE IF EXISTS `account_base_info`;
CREATE TABLE `account_base_info`
(
    `id`                  BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID of the user.',
    `phone_number`        VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin           DEFAULT NULL COMMENT 'Phone number of the user.',
    `encrypted_password`  VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'Encrypted password.',
    `username`            VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'Username.',
    `nickname`            VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'Nickname.',
    `avatar_url`          VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'URL of the avatar.',
    `email`               VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'Email of the user.',
    `phone_number_prefix` VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin  NOT NULL COMMENT 'Prefix of the phone number.',
    `gender`              VARCHAR(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'Gender of the user.',
    `state`               INT                                                    NOT NULL DEFAULT 0 COMMENT 'State of the account: 0 - normal; 1 - deleted',
    `creation_time`       DATETIME                                               NOT NULL DEFAULT NOW() COMMENT 'Creation time (registry time) of the account.',
    `update_time`         DATETIME                                               NOT NULL DEFAULT NOW() COMMENT 'Last update time.',
    KEY `idx_username` (`username`),
    KEY `idx_phone_number` (`phone_number`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_bin
    COMMENT = 'Base information of accounts.';

INSERT INTO `account_base_info`
(`phone_number`, `encrypted_password`, `username`, `nickname`, `avatar_url`, `email`, `phone_number_prefix`, `gender`)
VALUES ('13845123695', '{noop}123', 'admin', 'Admin', 'no_url', '22@qq.com', '86', 'male');