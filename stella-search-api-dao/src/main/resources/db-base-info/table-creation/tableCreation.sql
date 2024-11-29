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
    `name_in_oss`       VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'Name of the corresponding object in OSS.',
    `title`             VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT 'Title of the video.',
    `cover_url`         VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT 'URL of the cover of the video.',
    `creation_type_id`  BIGINT COMMENT 'Creation type of the video: 0 - Original; 1 - Reprint.',
    `section_id`        BIGINT COMMENT 'ID of the section that the video belongs to.',
    `label_ids`         VARCHAR(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin         DEFAULT NULL COMMENT 'Labels of the video, separated by ",", e.g., "Game,Challenge".',
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

DROP TABLE IF EXISTS `user_video_favorites`;
CREATE TABLE `user_video_favorites`
(
    `id`                BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID of the record.',
    `user_id`           BIGINT   NOT NULL COMMENT 'User ID, who adds the video to favorites.',
    `video_id`          BIGINT   NOT NULL COMMENT 'ID of the video that is added to favorites.',
    `playlist_id`       BIGINT   NOT NULL COMMENT 'ID of the playlist that contains the video.',
    `creator_id`        BIGINT   NOT NULL COMMENT 'ID of the creator of the record.',
    `creation_time`     DATETIME NOT NULL DEFAULT NOW() COMMENT 'Creation time of the record.',
    `modifier_id`       BIGINT   NOT NULL COMMENT 'ID of the user who modifies the record.',
    `modification_time` DATETIME NOT NULL DEFAULT NOW() COMMENT 'Last modification time of the record.',
    KEY `idx_user_id` (`user_id`),
    KEY `idx_video_id` (`video_id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_bin
    COMMENT = 'User video favorites.';

DROP TABLE IF EXISTS `user_video_playlist`;
CREATE TABLE `user_video_playlist`
(
    `id`                BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID of the record.',
    `user_id`           BIGINT       NOT NULL COMMENT 'ID of the user who owns the playlist.',
    `name`              VARCHAR(255) NOT NULL COMMENT 'Name of the playlist.',
    `description`       VARCHAR(500) NOT NULL COMMENT 'Description of the playlist.',
    `creation_time`     DATETIME     NOT NULL DEFAULT NOW() COMMENT 'Creation time of the record.',
    `modifier_id`       BIGINT       NOT NULL COMMENT 'ID of the user who modifies the record.',
    `modification_time` DATETIME     NOT NULL DEFAULT NOW() COMMENT 'Last modification time of the record.',
    KEY `idx_user_id` (`user_id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_bin
    COMMENT = 'Video playlists of users.';

DROP TABLE IF EXISTS `video_play_record`;
CREATE TABLE `video_play_record`
(
    `id`                BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID of the record.',
    `user_id`           BIGINT   NOT NULL COMMENT 'ID of the user who watched the video.',
    `video_id`          BIGINT   NOT NULL COMMENT 'ID of the video that is watched.',
    `creator_id`        BIGINT   NOT NULL COMMENT 'ID of the creator of the record.',
    `creation_time`     DATETIME NOT NULL DEFAULT NOW() COMMENT 'Creation time of the record.',
    `modifier_id`       BIGINT   NOT NULL COMMENT 'ID of the user who modifies the record.',
    `modification_time` DATETIME NOT NULL DEFAULT NOW() COMMENT 'Last modification time of the record.',
    KEY `idx_user_id` (`user_id`),
    KEY `video_id` (`video_id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_bin
    COMMENT = 'Play records of videos.';

DROP TABLE IF EXISTS `user_video_comment`;
CREATE TABLE `user_video_comment`
(
    `id`                BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID of the record.',
    `user_id`           BIGINT                                                 NOT NULL COMMENT 'ID of the user who comments.',
    `video_id`          BIGINT                                                 NOT NULL COMMENT 'ID of the video that the comment is associated with.',
    `content`           VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'Content of the comment.',
    `parent_id`         BIGINT                                                 NOT NULL DEFAULT -1 COMMENT 'ID of the parent comment. -1 if no parent.',
    `creator_id`        BIGINT                                                 NOT NULL COMMENT 'ID of the creator of the record.',
    `creation_time`     DATETIME                                               NOT NULL DEFAULT NOW() COMMENT 'Creation time of the record.',
    `modifier_id`       BIGINT                                                 NOT NULL COMMENT 'ID of the user who modifies the record.',
    `modification_time` DATETIME                                               NOT NULL DEFAULT NOW() COMMENT 'Last modification time of the record.',
    KEY `idx_creator_id` (`creator_id`),
    KEY `video_id` (`video_id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_bin
    COMMENT = 'Comments of videos.';

DROP TABLE IF EXISTS `user_video_like`;
CREATE TABLE `user_video_like`
(
    `id`                BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID of the record.',
    `user_id`           BIGINT   NOT NULL COMMENT 'ID of the user who gave the opinion, either like or dislike.',
    `video_id`          BIGINT   NOT NULL COMMENT 'ID of the video associated with the opinion.',
    `like_type`         TINYINT  NOT NULL COMMENT 'Like type: 1 - Like; 2 - Dislike.',
    `creator_id`        BIGINT   NOT NULL COMMENT 'ID of the creator of the record.',
    `creation_time`     DATETIME NOT NULL DEFAULT NOW() COMMENT 'Creation time of the record.',
    `modifier_id`       BIGINT   NOT NULL COMMENT 'ID of the user who modifies the record.',
    `modification_time` DATETIME NOT NULL DEFAULT NOW() COMMENT 'Last modification time of the record.',
    KEY `idx_user_id` (`user_id`),
    KEY `idx_video_id` (`video_id`),
    KEY `idx_like_type` (`like_type`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_bin
    COMMENT = 'Records of likes & dislikes of videos.';

DROP TABLE IF EXISTS `user_chat_session`;
CREATE TABLE `user_chat_session`
(
    `id`                BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID of the user chat session.',
    `user1_id`          BIGINT   NOT NULL COMMENT 'ID of the user1 who is in the chat.',
    `user2_id`          BIGINT   NOT NULL COMMENT 'ID of the user2 who chats with user1.',
    `state`             TINYINT  NOT NULL DEFAULT 1 COMMENT 'State of the chat session relationship: 0 - Blocked; 1 - Open.',
    `creator_id`        BIGINT   NOT NULL COMMENT 'ID of the creator of the record.',
    `creation_time`     DATETIME NOT NULL DEFAULT NOW() COMMENT 'Creation time of the record.',
    `modifier_id`       BIGINT   NOT NULL COMMENT 'ID of the user who modifies the record.',
    `modification_time` DATETIME NOT NULL DEFAULT NOW() COMMENT 'Last modification time of the record.',
    UNIQUE (`user1_id`, `user2_id`),
    KEY `idx_user1_id` (`user1_id`),
    KEY `idx_user2_id` (`user2_id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_bin
    COMMENT = 'Records of user chat session.';


DROP TABLE IF EXISTS `user_chat_message`;
CREATE TABLE `user_chat_message`
(
    `id`                BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID of the user chat message.',
    `session_id`        BIGINT                                                 NOT NULL COMMENT 'ID of the user chat session.',
    `sender_id`         BIGINT                                                 NOT NULL COMMENT 'ID of the user who sends the message.',
    `recipient_id`      BIGINT                                                 NOT NULL COMMENT 'ID of the user who receives the message.',
    `content`           VARCHAR(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'Content of the chat message.',
    `state`             TINYINT                                                NOT NULL DEFAULT 0 COMMENT 'State of the chat message relationship: 0 - Sent; 1 - Read; 2 - Blocked by blacklist.',
    `creator_id`        BIGINT                                                 NOT NULL COMMENT 'ID of the creator of the record.',
    `creation_time`     DATETIME                                               NOT NULL DEFAULT NOW() COMMENT 'Creation time of the record.',
    `modifier_id`       BIGINT                                                 NOT NULL COMMENT 'ID of the user who modifies the record.',
    `modification_time` DATETIME                                               NOT NULL DEFAULT NOW() COMMENT 'Last modification time of the record.',
    KEY `idx_session_id` (`session_id`),
    KEY `idx_sender_id` (`sender_id`),
    KEY `idx_receiver_id` (`recipient_id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_bin
    COMMENT = 'Records of user chat message.';

DROP TABLE IF EXISTS `user_following`;
CREATE TABLE `user_following`
(
    `id`                BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID of the record.',
    `user_id`           BIGINT   NOT NULL COMMENT 'ID of the user who follows another user.',
    `followed_user_id`  BIGINT   NOT NULL COMMENT 'ID of the user that is followed.',
    `following_status`  TINYINT  NOT NULL DEFAULT 1 COMMENT 'Status of the following relationship: 0 - Unfollowing; 1 - Following; 2 - Blocked.',
    `creator_id`        BIGINT   NOT NULL COMMENT 'ID of the creator of the record.',
    `creation_time`     DATETIME NOT NULL DEFAULT NOW() COMMENT 'Creation time of the record.',
    `modifier_id`       BIGINT   NOT NULL COMMENT 'ID of the user who modifies the record.',
    `modification_time` DATETIME NOT NULL DEFAULT NOW() COMMENT 'Last modification time of the record.',
    KEY `idx_user_id` (`user_id`),
    KEY `idx_following_user_id` (`followed_user_id`)

)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_bin
    COMMENT = 'Records of following other users.';

DROP TABLE IF EXISTS `user_blacklist`;
CREATE TABLE `user_blacklist`
(
    `id`                BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID of the user blacklist record.',
    `user_id`           BIGINT   NOT NULL COMMENT 'User ID of the person initiating the block.',
    `blocked_user_id`   BIGINT   NOT NULL COMMENT 'User ID of the person being blocked.',
    `creator_id`        BIGINT   NOT NULL COMMENT 'ID of the creator of the record.',
    `creation_time`     DATETIME NOT NULL DEFAULT NOW() COMMENT 'Creation time of the record.',
    `modifier_id`       BIGINT   NOT NULL COMMENT 'ID of the user who modifies the record.',
    `modification_time` DATETIME NOT NULL DEFAULT NOW() COMMENT 'Last modification time of the record.',
    KEY `idx_user_id` (`user_id`),
    KEY `idx_blocked_user_id` (`blocked_user_id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_bin
    COMMENT = 'Records of user blacklist info.';