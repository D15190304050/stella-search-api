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

DROP TABLE IF EXISTS `video_uploading_task`;
CREATE TABLE `video_uploading_task`
(
    `id`                BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'Auto increment ID.',
    `task_id`           VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'Task ID.',
    `state`             INT                                                    NOT NULL COMMENT 'State of the video uploading task: 0 - Created; 1 - Completed; 2 - Aborted.',
    `creator_id`        BIGINT                                                 NOT NULL COMMENT 'ID of the creator of the video uploading task, i.e., ID of who uploads the video.',
    `creation_time`     DATETIME                                               NOT NULL DEFAULT NOW() COMMENT 'Creation time of the video uploading task.',
    `modifier_id`       BIGINT                                                 NOT NULL COMMENT 'ID of the modifier of the video uploading task, i.e., ID of who uploads the video.',
    `modification_time` DATETIME                                               NOT NULL DEFAULT NOW() COMMENT 'Modification time of the video uploading task.',
    KEY `idx_task_id` (`task_id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_bin
    COMMENT = 'Base information of accounts.';

DROP TABLE IF EXISTS `user_video_info`;
CREATE TABLE `user_video_info`
(
    `id`                BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID of the video.',
    `video_url`         VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'URL of the video.',
    `title`             VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT 'Title of the video.',
    `cover_url`         VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT 'URL of the cover of the video.',
    `creation_type_id`     BIGINT COMMENT 'Creation type of the video: 0 - Original; 1 - Reprint.',
    `section_id`        BIGINT COMMENT 'ID of the section that the video belongs to.',
    `label_ids`            VARCHAR(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin         DEFAULT NULL COMMENT 'Labels of the video, separated by ",", e.g., "Game,Challenge".',
    `introduction`      VARCHAR(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin         DEFAULT NULL COMMENT 'Introduction to the video.',
    `creator_id`        BIGINT                                                 NOT NULL COMMENT 'ID of the user who uploads the video.',
    `creation_time`     DATETIME                                               NOT NULL DEFAULT NOW() COMMENT 'Creation time of the video uploading task.',
    `modifier_id`       BIGINT                                                 NOT NULL COMMENT 'ID of the modifier of the video information, i.e., ID of the user who uploads the video information.',
    `modification_time` DATETIME                                               NOT NULL DEFAULT NOW() COMMENT 'Modification time of the video information.',
    KEY `idx_creator_id` (`creator_id`),
    KEY `idx_section_id` (`section_id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_bin
    COMMENT = 'Information of user uploaded videos.';

DROP TABLE IF EXISTS `video_creation_type`;
CREATE TABLE `video_creation_type`
(
    `id`                BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID of the video creation type.',
    `type`              VARCHAR(20) NOT NULL COMMENT 'Video creation type: 0 - Original; 1 - Reprinting.',
    `creator_id`        BIGINT      NOT NULL COMMENT 'ID of the user who uploads the video.',
    `creation_time`     DATETIME    NOT NULL DEFAULT NOW() COMMENT 'Creation time of the video uploading task.',
    `modifier_id`       BIGINT      NOT NULL COMMENT 'ID of the modifier of the video information, i.e., ID of the user who uploads the video information.',
    `modification_time` DATETIME    NOT NULL DEFAULT NOW() COMMENT 'Modification time of the video information.'
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_bin
    COMMENT = 'Video creation types.';

INSERT INTO `video_creation_type`
    (`type`, `creator_id`, `modifier_id`)
VALUES ('Original', 1, 1),
       ('Reprinting', 1, 1);

DROP TABLE IF EXISTS `video_section`;
CREATE TABLE `video_section`
(
    `id`                BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID of the video creation type.',
    `section`           VARCHAR(200) NOT NULL COMMENT 'Section of the video: 0 - None; 1 - Games; 2 - Action; 3 - Animation.',
    `creator_id`        BIGINT       NOT NULL COMMENT 'ID of the user who uploads the video.',
    `creation_time`     DATETIME     NOT NULL DEFAULT NOW() COMMENT 'Creation time of the video uploading task.',
    `modifier_id`       BIGINT       NOT NULL COMMENT 'ID of the modifier of the video information, i.e., ID of the user who uploads the video information.',
    `modification_time` DATETIME     NOT NULL DEFAULT NOW() COMMENT 'Modification time of the video information.'
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_bin
    COMMENT = 'Video sections.';

INSERT INTO `video_section`
    (`section`, `creator_id`, `modifier_id`)
VALUES ('None', 1, 1),
       ('Games', 1, 1),
       ('Action', 1, 1),
       ('Animation', 1, 1);

DROP TABLE IF EXISTS `video_label`;
CREATE TABLE `video_label`
(
    `id`                BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID of the video creation type.',
    `label`             VARCHAR(100) NOT NULL COMMENT 'The label.',
    `creator_id`        BIGINT       NOT NULL COMMENT 'ID of the user who uploads the video.',
    `creation_time`     DATETIME     NOT NULL DEFAULT NOW() COMMENT 'Creation time of the video uploading task.',
    `modifier_id`       BIGINT       NOT NULL COMMENT 'ID of the modifier of the video information, i.e., ID of the user who uploads the video information.',
    `modification_time` DATETIME     NOT NULL DEFAULT NOW() COMMENT 'Modification time of the video information.'
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_bin
    COMMENT = 'Video labels.';

INSERT INTO `video_label`
    (`label`, `creator_id`, `modifier_id`)
VALUES ('Games', 1, 1),
       ('Action', 1, 1),
       ('Animation', 1, 1);