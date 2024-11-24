ALTER TABLE `user_video_info`
    ADD COLUMN `summary_file_name` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin COMMENT 'Path of the summary file of the video.';