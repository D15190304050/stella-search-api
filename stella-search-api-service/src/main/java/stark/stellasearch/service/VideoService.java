package stark.stellasearch.service;

import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import stark.dataworks.basic.data.json.JsonSerializer;
import stark.dataworks.basic.data.redis.RedisQuickOperation;
import stark.dataworks.basic.params.OutValue;
import stark.dataworks.boot.autoconfig.minio.EasyMinio;
import stark.dataworks.boot.autoconfig.web.LogArgumentsAndResponse;
import stark.dataworks.boot.web.PaginatedData;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.dao.*;
import stark.stellasearch.domain.UserVideoLike;
import stark.stellasearch.dao.VideoPlayRecordMapper;
import stark.stellasearch.domain.UserVideoInfo;
import stark.stellasearch.domain.VideoPlayRecord;
import stark.stellasearch.domain.entities.es.VideoSummaryInfo;
import stark.stellasearch.dto.params.*;
import stark.stellasearch.dto.results.TopicSummaryVideoStartMessage;
import stark.stellasearch.dto.results.TranscriptSummary;
import stark.stellasearch.dto.results.VideoPlayInfo;
import stark.stellasearch.service.dto.User;
import stark.stellasearch.service.kafka.ProducerService;

import jakarta.validation.Valid;
import stark.stellasearch.service.redis.RedisKeyManager;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@LogArgumentsAndResponse
@Validated
public class VideoService
{
    public static final String VIDEO_UPLOAD_TASK_ID_PREFIX = "videoUpload";
    public static final String VIDEO_CHUNK_COUNT_PREFIX = "chunkCount";
    public static final String VIDEO_CHUNK_SET_PLACEHOLDER = "-1";
    public static final String VIDEO_FILE_EXTENSION = "-extension";
    public static final Set<String> VIDEO_FILE_EXTENSION_SET = Set.of(".mp4", ".avi");
    public static final int VIDEO_LIKE = 1;

    @Value("${dataworks.easy-minio.bucket-name-videos}")
    private String bucketNameVideos;

    @Value("${dataworks.easy-minio.bucket-name-summaries}")
    private String bucketNameSummaries;

    @Value("${spring.kafka.producer.topic-summary-video-start}")
    private String topicSummaryVideoStart;

    @Autowired
    private RedisQuickOperation redisQuickOperation;

    @Autowired
    private EasyMinio easyMinio;

    @Autowired
    @Qualifier("lowPriorityTaskExecutor")
    private ThreadPoolTaskExecutor lowPriorityTaskExecutor;

    @Autowired
    private UserVideoInfoMapper userVideoInfoMapper;

    @Autowired
    private UserVideoLikeMapper userVideoLikeMapper;

    @Autowired
    private VideoPlayRecordMapper videoPlayRecordMapper;
    @Autowired
    private ProducerService producerService;

    @Autowired
    private UserVideoPlaylistMapper userVideoPlaylistMapper;

    @Autowired
    private UserVideoFavoritesMapper userVideoFavoritesMapper;

    @Autowired
    private RedisKeyManager redisKeyManager;

    private String generateTaskId(long userId)
    {
        String taskIdPrefix = VIDEO_UPLOAD_TASK_ID_PREFIX + "-" + userId + "-" + System.currentTimeMillis() + "-";

        String taskId;
        do
            taskId = taskIdPrefix + UUID.randomUUID();
        while (redisQuickOperation.containsKey(taskId));

        return taskId;
    }

    private String generateTaskChunkCountKey(String taskId)
    {
        return taskId.replace(VIDEO_UPLOAD_TASK_ID_PREFIX, VIDEO_CHUNK_COUNT_PREFIX);
    }

    public ServiceResponse<String> generateNewVideoUploadingTask(@Valid NewVideoUploadingTaskRequest request)
    {
        String fileExtension = request.getVideoFileExtension();
        if (!VIDEO_FILE_EXTENSION_SET.contains(fileExtension))
            return ServiceResponse.buildErrorResponse(-3, "Unacceptable video file extension: " + fileExtension);

        User currentUser = UserContextService.getCurrentUser();
        long userId = currentUser.getId();

        String taskId = generateTaskId(userId);
        String chunkCountKey = generateTaskChunkCountKey(taskId);

        // Initial state of uploading task.
        redisQuickOperation.set(taskId + VIDEO_FILE_EXTENSION, fileExtension, 30, TimeUnit.MINUTES);
        redisQuickOperation.set(chunkCountKey, request.getVideoChunkCount(), 30, TimeUnit.MINUTES);
        redisQuickOperation.setAdd(taskId, VIDEO_CHUNK_SET_PLACEHOLDER);
        resetExpirationOfUploadingTask(taskId);

        return ServiceResponse.buildSuccessResponse(taskId);
    }

    public ServiceResponse<Boolean> uploadVideoChunk(@Valid VideoChunkUploadingRequest request) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
    {
        // Validations:
        // 1. The task ID must exist.
        // 2. The chunk index must between 0 and (chunkCount - 1).

        // Validation 1.
        String taskId = request.getVideoUploadingTaskId();
        if (!redisQuickOperation.containsKey(taskId))
            return ServiceResponse.buildErrorResponse(-2, "Invalid video uploading task ID: " + taskId);

        // Validation 2.
        long videoChunkIndex = request.getVideoChunkIndex();
        String chunkCountKey = generateTaskChunkCountKey(taskId);
        long chunkCount = Long.parseLong(redisQuickOperation.get(chunkCountKey));
        if (videoChunkIndex >= chunkCount || videoChunkIndex < 0)
            return ServiceResponse.buildErrorResponse(-2, "Invalid video chunk Index: " + videoChunkIndex);

        String chunkNamePrefix = taskId + "-";
        String chunkName = chunkNamePrefix + videoChunkIndex;

        // TODO: Adjustment of uploading state.
        // 1. If the chunkSize % 10 == 0 or uploading ends, write uploaded chunk ids to database, in case Redis is down.

        // Steps:
        // 1. Check if the chunk is uploaded. If it is, return success.
        // 2. Upload the chunk if it was not uploaded.
        // 2.1 Upload it to MinIO.
        // 2.2 Add it to redis state set.
        // 2.3 Reset the expiration time of the redis set.

        // Step 1.
        if (redisQuickOperation.setContains(taskId, "" + videoChunkIndex))
            return ServiceResponse.buildSuccessResponse(true);

        // Step 2.
        easyMinio.uploadFileByStream(bucketNameVideos, chunkName, request.getVideoChunk().getInputStream());
        redisQuickOperation.setAdd(taskId, chunkName);
        resetExpirationOfUploadingTask(taskId);

        return ServiceResponse.buildSuccessResponse(true);
    }

    /**
     * If all the chunks are uploaded, compose them, and delete the set.
     *
     * @param request
     * @return
     * @throws ServerException
     * @throws InsufficientDataException
     * @throws ErrorResponseException
     * @throws IOException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws XmlParserException
     * @throws InternalException
     */
    public ServiceResponse<Long> composeVideoChunks(@Valid ComposeVideoChunksRequest request) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
    {
        String taskId = request.getVideoUploadingTaskId();
        if (!redisQuickOperation.containsKey(taskId))
            return ServiceResponse.buildErrorResponse(-2, "Invalid video uploading task ID: " + taskId);

        String chunkNamePrefix = taskId + "-";

        String chunkCountKey = generateTaskChunkCountKey(taskId);
        long chunkCount = Long.parseLong(redisQuickOperation.get(chunkCountKey));

        Long uploadedChunkCount = redisQuickOperation.setCount(taskId);
        if (uploadedChunkCount == null)
            return ServiceResponse.buildErrorResponse(-3, "Unable to get video chunk count.");

        // Add 1 because there is a placeholder "-1" to create the set.
        if (chunkCount + 1 != uploadedChunkCount)
            return ServiceResponse.buildErrorResponse(-3, String.format("It needs all %d chunks uploaded before composing, but now only %d chunks are uploaded", chunkCount, uploadedChunkCount));

        // Get chunk names.
        // Note: we should remove the placeholder before convert chunks to video file.
        Set<String> chunkNames = redisQuickOperation.setGetAll(taskId);
        chunkNames.remove(VIDEO_CHUNK_SET_PLACEHOLDER);
        List<String> sortedChunkNames = new ArrayList<>(chunkNames);

        // Get videoFileExtensionKey & videoName.
        String videoFileExtensionKey = taskId + VIDEO_FILE_EXTENSION;
        String videoFileExtension = redisQuickOperation.get(videoFileExtensionKey);
        String videoName = taskId + videoFileExtension;

        if (chunkCount == 1)
            easyMinio.copyObject(bucketNameVideos, sortedChunkNames.get(0), videoName);
        else
        {
            int chunkNamePrefixLength = chunkNamePrefix.length();

            sortedChunkNames.sort((x, y) ->
            {
                int xIndex = Integer.parseInt(x.substring(chunkNamePrefixLength));
                int yIndex = Integer.parseInt(y.substring(chunkNamePrefixLength));

                return xIndex - yIndex;
            });

            easyMinio.composeObjects(bucketNameVideos, videoName, sortedChunkNames);
        }

        // Remove the keys in the redis once uploading finished.
        clearUploadingTask(taskId, sortedChunkNames);

        // Save user-video info.
        long videoId = saveUserVideoInfo(videoName);

        return ServiceResponse.buildSuccessResponse(videoId);
    }

    /**
     * Save user-video info.
     *
     * @param videoName
     */
    private long saveUserVideoInfo(String videoName)
    {
        // TODO: Validate if the video name exists in the database (i.e. in MinIO).
        // We use UUID here, so the probability of collision is relatively small.

        User currentUser = UserContextService.getCurrentUser();
        Date now = new Date();

        UserVideoInfo userVideoInfo = new UserVideoInfo();

        userVideoInfo.setNameInOss(videoName);
        userVideoInfo.setCreatorId(currentUser.getId());
        userVideoInfo.setCreationTime(now);
        userVideoInfo.setModifierId(currentUser.getId());
        userVideoInfo.setModificationTime(now);

        userVideoInfoMapper.insert(userVideoInfo);

        return userVideoInfo.getId();
    }

    private void resetExpirationOfUploadingTask(String taskId)
    {
        redisQuickOperation.expire(taskId, 30, TimeUnit.MINUTES);
    }

    /**
     * Clear the resources of the video uploading task, which will not be used again.
     * The resources contain keys in redis, a record in database, and chunks in MinIO.
     *
     * @param taskId
     */
    private void clearUploadingTask(String taskId, List<String> sortedChunkNames)
    {
        // TODO: Clear the database record.

        lowPriorityTaskExecutor.execute(() ->
        {
            String chunkCountKey = generateTaskChunkCountKey(taskId);
            String videoFileExtensionKey = taskId + VIDEO_FILE_EXTENSION;

            redisQuickOperation.delete(taskId);
            redisQuickOperation.delete(chunkCountKey);
            redisQuickOperation.delete(videoFileExtensionKey);

            // Remove chunks in MinIO (compose operation will only create a new object without any changes on existing objects.)
            try
            {
                easyMinio.deleteObjects(bucketNameVideos, sortedChunkNames);
            }
            catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                   NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                   InternalException e)
            {
                throw new RuntimeException(e);
            }
        });
    }

    public ServiceResponse<Boolean> setVideoInfo(@Valid VideoInfoFormData request) throws ExecutionException, InterruptedException
    {
        long videoId = request.getVideoId();
        OutValue<UserVideoInfo> userVideoInfoOutValue = new OutValue<>();

        String errorMessage = validateVideoLabels(request.getLabels());
        if (errorMessage != null)
            return ServiceResponse.buildErrorResponse(-2, errorMessage);

        errorMessage = validateVideoInfoForInitialization(videoId, userVideoInfoOutValue);
        if (errorMessage != null)
            return ServiceResponse.buildErrorResponse(-2, errorMessage);

        ServiceResponse<Boolean> updatedVideoInfoResponse = updateVideoInfoResponse(request, userVideoInfoOutValue.getValue());
        if (!updatedVideoInfoResponse.isSuccess())
            return updatedVideoInfoResponse;

        UserVideoInfo userVideoInfo = userVideoInfoMapper.getVideoBaseInfoById(videoId);
        if (userVideoInfo == null)
            return ServiceResponse.buildErrorResponse(-2, "Invalid video ID: " + videoId);

        sendSummaryStartMessage(videoId, userVideoInfo.getNameInOss());

        return ServiceResponse.buildSuccessResponse(true);
    }

    private void sendSummaryStartMessage(long videoId, String videoName) throws ExecutionException, InterruptedException
    {
        TopicSummaryVideoStartMessage message = new TopicSummaryVideoStartMessage();
        message.setVideoId(videoId);
        message.setVideoObjectName(videoName);
        String messageContent = JsonSerializer.serialize(message);
        producerService.sendMessage(topicSummaryVideoStart, messageContent);
    }

    private ServiceResponse<Boolean> updateVideoInfoResponse(VideoInfoFormData request, UserVideoInfo userVideoInfo)
    {
        updateVideoInfo(request, userVideoInfo);
        return ServiceResponse.buildSuccessResponse(true);
    }

    private void updateVideoInfo(VideoInfoFormData request, UserVideoInfo userVideoInfo)
    {
        List<Long> labels = request.getLabels();
        labels.sort(Long::compareTo);
        String labelArrayText = JsonSerializer.serialize(labels);

        userVideoInfo.setTitle(request.getTitle());
        userVideoInfo.setCoverUrl(request.getCoverUrl());
        userVideoInfo.setCreationTypeId(request.getVideoCreationType());
        userVideoInfo.setSectionId(request.getSection());
        userVideoInfo.setLabelIds(labelArrayText);
        userVideoInfo.setIntroduction(request.getIntroduction());

        userVideoInfoMapper.updateVideoInfoById(userVideoInfo);
    }

    private String validateVideoInfoForInitialization(long videoId, OutValue<UserVideoInfo> userVideoInfoOutValue)
    {
        // Validations:
        // 1. The video with the specified ID exists.
        // 2. User uploaded this video.
        // 3. The video information is not set (i.e. title).
        // Validations 1, 2 are implemented in the method validateVideoInfoForCreator().

        // Validation 1 & 2.
        String errorMessage = validateVideoInfoForCreator(videoId, userVideoInfoOutValue);
        if (errorMessage != null)
            return errorMessage;

        UserVideoInfo videoInfo = userVideoInfoOutValue.getValue();

        // region Validation 3.
        if (videoInfo.getTitle() != null)
            return "You have already set up video information with title: " + videoInfo.getTitle();
        // endregion

        return null;
    }

    private String validateVideoLabels(List<Long> labels)
    {
        if (CollectionUtils.isEmpty(labels))
            return "There are no video labels.";

        for (Long label : labels)
        {
            if (label == null || label <= 0)
                return "Invalid label ID: " + label + ". We only accept positive label IDs.";
        }

        return null;
    }

    private String validateVideoInfoForCreator(long videoId, OutValue<UserVideoInfo> userVideoInfoOutValue)
    {
        // Validations:
        // 1. Video exists.
        // 2. Video creator ID = current user ID.

        // region Validation 1.
        String errorMessage = validateVideoIdExistence(videoId, userVideoInfoOutValue);
        if (errorMessage != null)
            return errorMessage;
        // endregion

        // region Validation 2.
        UserVideoInfo userVideoInfo = userVideoInfoOutValue.getValue();

        if (userVideoInfo.getCreatorId() != UserContextService.getCurrentUser().getId())
            return "You can only update information of videos uploaded by yourself.";
        // endregion

        return null;
    }

    private String validateVideoInfoForUpdate(long videoId, OutValue<UserVideoInfo> userVideoInfoOutValue)
    {
        // Validations:
        // 1. Video exists.
        // 2. Video creator ID = current user ID.
        // 3. User uploaded this video (=> title != null).

        // Validation 1.
        String errorMessage = validateVideoInfoForCreator(videoId, userVideoInfoOutValue);
        if (errorMessage != null)
            return errorMessage;

        // Validation 3.
        if (userVideoInfoOutValue.getValue().getTitle() == null)
            return "You can only update information of existing video.";

        return null;
    }

    public ServiceResponse<List<VideoPlayInfo>> getVideoInfoOfCurrentUser(@Valid PaginationRequestParam request)
    {
        GetVideoInfosByUserIdQueryParam queryParam = new GetVideoInfosByUserIdQueryParam();
        queryParam.setUserId(UserContextService.getCurrentUser().getId());
        queryParam.setPaginationParam(request);

        List<VideoPlayInfo> videoPlayInfos = userVideoInfoMapper.getVideoPlayInfosByUserId(queryParam);
        ServiceResponse<List<VideoPlayInfo>> response = ServiceResponse.buildSuccessResponse(videoPlayInfos);
        response.putExtra("size", videoPlayInfos.size());

        return response;
    }

    public ServiceResponse<Long> countVideoByUserId()
    {
        Long count = userVideoInfoMapper.countVideoByUserId(UserContextService.getCurrentUser().getId());
        return ServiceResponse.buildSuccessResponse(count);
    }

    public ServiceResponse<Boolean> updateVideoInfo(@Valid VideoInfoFormData request)
    {
        String errorMessage = validateVideoLabels(request.getLabels());
        if (errorMessage != null)
            return ServiceResponse.buildErrorResponse(-7, errorMessage);

        OutValue<UserVideoInfo> userVideoInfoOutValue = new OutValue<>();
        errorMessage = validateVideoInfoForUpdate(request.getVideoId(), userVideoInfoOutValue);
        if (errorMessage != null)
            return ServiceResponse.buildErrorResponse(-6, errorMessage);

        return updateVideoInfoResponse(request, userVideoInfoOutValue.getValue());
    }

    public ServiceResponse<VideoInfoFormData> getVideoInfoFormDataById(long videoId)
    {
        OutValue<UserVideoInfo> userVideoInfoOutValue = new OutValue<>();
        String errorMessage = validateVideoIdExistence(videoId, userVideoInfoOutValue);
        if (errorMessage != null)
            return ServiceResponse.buildErrorResponse(-6, errorMessage);

        VideoInfoFormData request = convertToVideoInfoFormData(userVideoInfoOutValue.getValue());
        return ServiceResponse.buildSuccessResponse(request);
    }

    private static VideoInfoFormData convertToVideoInfoFormData(UserVideoInfo userVideoInfo)
    {
        String labelIds = userVideoInfo.getLabelIds();
        List<Long> labels = JsonSerializer.deserializeList(labelIds, Long.class);
        labels.sort(Long::compareTo);

        VideoInfoFormData formData = new VideoInfoFormData();

        formData.setVideoId(userVideoInfo.getId());
        formData.setTitle(userVideoInfo.getTitle());
        formData.setCoverUrl(userVideoInfo.getCoverUrl());
        formData.setIntroduction(userVideoInfo.getIntroduction());
        formData.setSection(userVideoInfo.getSectionId());
        formData.setVideoCreationType(userVideoInfo.getCreationTypeId());
        formData.setLabels(labels);

        return formData;
    }

    private String validateVideoIdExistence(long videoId, OutValue<UserVideoInfo> userVideoInfoOutValue)
    {
        if (videoId <= 0)
            return "Invalid video ID: " + videoId;

        UserVideoInfo userVideoInfo = userVideoInfoMapper.getVideoBaseInfoById(videoId);
        if (userVideoInfo == null)
            return "Invalid video ID: " + videoId;

        userVideoInfoOutValue.setValue(userVideoInfo);
        return null;
    }

    public ServiceResponse<VideoPlayInfo> getVideoPlayInfoById(long videoId) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
    {
        long userId = UserContextService.getCurrentUser().getId();

        VideoPlayInfo videoPlayInfo = userVideoInfoMapper.getVideoPlayInfoById(videoId, userId);
        if (videoPlayInfo == null)
            return ServiceResponse.buildErrorResponse(-7, "Invalid video ID: " + videoId);

        String videoPlayUrlKey = redisKeyManager.getVideoPlayUrlKey(videoId);
        String videoPlayUrl = redisQuickOperation.get(videoPlayUrlKey);

        // TODO: We may need a distributed lock here to prevent concurrent query of same object urls.
        if (videoPlayUrl == null)
        {
            videoPlayUrl = easyMinio.getObjectUrl(bucketNameVideos, videoPlayInfo.getNameInOss());
            redisQuickOperation.set(videoPlayUrlKey, videoPlayUrl, 5, TimeUnit.MINUTES);
        }

        videoPlayInfo.setVideoPlayUrl(videoPlayUrl);

        String errorMessage = saveVideoPlayRecord(videoPlayInfo);
        if (errorMessage != null)
            return ServiceResponse.buildErrorResponse(-1, errorMessage);

        videoPlayInfo.setPlayCount(videoPlayInfo.getPlayCount() + 1);
        return ServiceResponse.buildSuccessResponse(videoPlayInfo);
    }

    public ServiceResponse<Boolean> likeVideo(@Valid LikeVideoRequest request)
    {
        long videoId = request.getVideoId();

        long videoCount = userVideoInfoMapper.countVideoById(videoId);
        if (videoCount == 0)
            return ServiceResponse.buildErrorResponse(-8, "Invalid video ID: " + videoId);

        // Validate if the video is liked before by the same user.
        long userVideoLikeCount = userVideoLikeMapper.countUserVideoLike(UserContextService.getCurrentUser().getId(), videoId);
        if (userVideoLikeCount != 0)
            return ServiceResponse.buildSuccessResponse(true);

        String errorMessage = insertVideoLike(videoId);
        if (errorMessage != null)
            return ServiceResponse.buildErrorResponse(-8, errorMessage);

        return ServiceResponse.buildSuccessResponse(true);
    }

    private String insertVideoLike(long videoId)
    {
        long userId = UserContextService.getCurrentUser().getId();

        UserVideoLike userVideoLikeInfo = new UserVideoLike();
        Date now = new Date();
        userVideoLikeInfo.setUserId(userId);
        userVideoLikeInfo.setVideoId(videoId);
        userVideoLikeInfo.setLikeType(VIDEO_LIKE);
        userVideoLikeInfo.setCreatorId(userId);
        userVideoLikeInfo.setCreationTime(now);
        userVideoLikeInfo.setModifierId(userId);
        userVideoLikeInfo.setModificationTime(now);

        int result = userVideoLikeMapper.insertLike(userVideoLikeInfo);
        if (result != 1)
            return "Failed to like video.";

        return null;
    }

    public ServiceResponse<Boolean> cancelLikeVideo(@Valid CancelLikeVideoRequest request)
    {
        // TODO: Add 1 more validation => validate if the user likes the video.
        long videoId = request.getVideoId();
        long videoCount = userVideoInfoMapper.countVideoById(videoId);
        if (videoCount == 0)
            return ServiceResponse.buildErrorResponse(-8, "Invalid video ID: " + videoId);

        userVideoLikeMapper.deleteLike(UserContextService.getCurrentUser().getId(), videoId);
        return ServiceResponse.buildSuccessResponse(true);
    }

    /**
     * Insert record to table of video play count
     *
     * @param videoPlayInfo
     * @return
     */
    private String saveVideoPlayRecord(VideoPlayInfo videoPlayInfo)
    {
        Date now = new Date();
        VideoPlayRecord videoPlayRecord = new VideoPlayRecord();
        videoPlayRecord.setUserId(UserContextService.getCurrentUser().getId());
        videoPlayRecord.setVideoId(videoPlayInfo.getId());
        videoPlayRecord.setCreationTime(now);
        videoPlayRecord.setCreatorId(videoPlayInfo.getCreatorId());
        videoPlayRecord.setModificationTime(now);

        if (videoPlayRecordMapper.insert(videoPlayRecord) != 1)
            return "Insert record to table of video play count failed";

        return null;
    }

    // TODO: Add visible options for playlists belonging to others.
    public ServiceResponse<PaginatedData<VideoPlayInfo>> getVideoPlayInfoInPlaylist(@Valid GetVideoPlayInfoInPlaylistRequest request)
    {
        long playlistId = request.getPlaylistId();

        long playlistCount = userVideoPlaylistMapper.countPlaylistById(playlistId);
        if (playlistCount == 0)
            return ServiceResponse.buildErrorResponse(-1, "The playlist with ID " + playlistId + " does not exist.");

        GetVideoPlayInfoInPlaylistQueryParam queryParam = new GetVideoPlayInfoInPlaylistQueryParam();
        queryParam.setUserId(UserContextService.getCurrentUser().getId());
        queryParam.setPlaylistId(playlistId);
        queryParam.setPaginationParam(request);
        List<VideoPlayInfo> videoPlayInfosInPlaylist = userVideoInfoMapper.getVideoPlayInfosByPlaylistId(queryParam);

        long videoCountInPlaylist = userVideoFavoritesMapper.countVideosInPlaylist(playlistId);

        PaginatedData<VideoPlayInfo> paginatedData = new PaginatedData<>();
        paginatedData.setData(videoPlayInfosInPlaylist);
        paginatedData.setTotal(videoCountInPlaylist);

        return ServiceResponse.buildSuccessResponse(paginatedData);
    }

    public ServiceResponse<TranscriptSummary> getSummaryOfVideo(long videoId) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
    {
        String videoSummaryFileName = userVideoInfoMapper.getVideoSummaryFileNameById(videoId);
        if (videoSummaryFileName == null)
            return ServiceResponse.buildErrorResponse(-1, "There is no related summary for the video.");

        TranscriptSummary summary = easyMinio.getObject(bucketNameSummaries, videoSummaryFileName, TranscriptSummary.class);
        return ServiceResponse.buildSuccessResponse(summary);
    }
}
