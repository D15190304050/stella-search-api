DROP TABLE IF EXISTS `video_uploading_task`;
CREATE TABLE `video_uploading_task`
(
    `id`                BIGINT       PRIMARY KEY AUTO_INCREMENT COMMENT 'Auto increment ID.',
    `task_id`           VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL COMMENT 'Task ID.',
    `state`             INT          NOT NULL COMMENT 'State of the video uploading task: 0 - Created; 1 - Completed; 2 - Aborted.',
    `creator_id`        BIGINT       NOT NULL COMMENT 'ID of the creator of the video uploading task, i.e., ID of who uploads the video.',
    `creation_time`     DATETIME     NOT NULL COMMENT 'Creation time of the video uploading task.',
    `modifier_id`       BIGINT       NOT NULL COMMENT 'ID of the modifier of the video uploading task, i.e., ID of who uploads the video.',
    `modification_time` DATETIME     NOT NULL COMMENT 'Modification time of the video uploading task.',
    KEY `idx_task_id` (`task_id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8mb4
    COLLATE = utf8mb4_bin
    COMMENT = 'Base information of accounts.';