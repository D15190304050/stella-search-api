package stark.stellasearch.service;

import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import stark.dataworks.basic.data.json.JsonSerializer;
import stark.dataworks.basic.data.redis.RedisQuickOperation;
import stark.dataworks.basic.params.OutValue;
import stark.dataworks.boot.autoconfig.minio.EasyMinio;
import stark.dataworks.boot.autoconfig.web.LogArgumentsAndResponse;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.dao.UserVideoInfoMapper;
import stark.stellasearch.domain.UserVideoInfo;
import stark.stellasearch.dto.params.*;
import stark.stellasearch.dto.results.VideoInfo;
import stark.stellasearch.service.dto.User;

import javax.validation.Valid;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
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

    @Value("${dataworks.easy-minio.bucket-name-videos}")
    private String bucketNameVideos;

    @Value("${videos.video-prefix}")
    private String videoStreamPrefix;

    @Autowired
    private RedisQuickOperation redisQuickOperation;

    @Autowired
    private EasyMinio easyMinio;

    @Autowired
    @Qualifier("lowPriorityTaskExecutor")
    private ThreadPoolTaskExecutor lowPriorityTaskExecutor;

    @Autowired
    private UserVideoInfoMapper userVideoInfoMapper;

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
     * @param videoName
     */
    private long saveUserVideoInfo(String videoName)
    {
        User currentUser = UserContextService.getCurrentUser();
        Date now = new Date();

        UserVideoInfo userVideoInfo = new UserVideoInfo();

        userVideoInfo.setVideoUrl(videoStreamPrefix + videoName);
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
     * @param taskId
     */
    private void clearUploadingTask(String taskId, List<String> sortedChunkNames) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
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

    public ServiceResponse<Boolean> setVideoInfo(@Valid SetVideoInfoRequest request)
    {
        OutValue<String> message = new OutValue<>();
        OutValue<UserVideoInfo> userVideoInfoOutValue = new OutValue<>();

        if (!validateVideoInfo(request, message, userVideoInfoOutValue))
        {
            log.error("Validation error: {}", message.getValue());
            return ServiceResponse.buildErrorResponse(-100, message.getValue());
        }

        updateVideoInfo(request, userVideoInfoOutValue);

        return ServiceResponse.buildSuccessResponse(true);
    }

    private void updateVideoInfo(SetVideoInfoRequest request, OutValue<UserVideoInfo> userVideoInfoOutValue)
    {
        List<Long> labels = request.getLabels();
        String labelArrayText = JsonSerializer.serialize(labels);

        UserVideoInfo videoInfo = userVideoInfoOutValue.getValue();
        videoInfo.setTitle(request.getTitle());
        videoInfo.setCoverUrl(request.getCoverUrl());
        videoInfo.setCreationTypeId(request.getVideoCreationType());
        videoInfo.setSectionId(request.getSection());
        videoInfo.setLabelIds(labelArrayText);
        videoInfo.setIntroduction(request.getIntroduction());

        userVideoInfoMapper.updateVideoInfoById(videoInfo);
    }

    private boolean validateVideoInfo(SetVideoInfoRequest request, OutValue<String> message, OutValue<UserVideoInfo> userVideoInfoOutValue)
    {
        // Validations:
        // 1. All labels must be positive.
        // 2. The video with the specified ID exists.
        // 3. User uploaded this video.
        // 4. The video information is not set (i.e. cover URL).

        // region Validation 1.
        List<Long> labels = request.getLabels();

        for (Long label : labels)
        {
            if (label == null || label <= 0)
            {
                message.setValue("Invalid label ID: " + label + ". We only accept positive label IDs.");
                return false;
            }
        }
        // endregion

        // region Validation 2.
        long videoId = request.getVideoId();
        UserVideoInfo videoInfo = userVideoInfoMapper.getVideoInfoById(videoId);
        userVideoInfoOutValue.setValue(videoInfo);

        if (videoInfo == null)
        {
            message.setValue("There is no video with id: " + videoId);
            return false;
        }
        // endregion

        // region Validation 3.
        User currentUser = UserContextService.getCurrentUser();
        if (videoInfo.getCreatorId() != currentUser.getId())
        {
            message.setValue("You can only set up videos with the same creator id.");
            return false;
        }
        // endregion

        // region Validation 4.
        if (videoInfo.getCoverUrl() != null)
        {
            message.setValue("You have already set up video information with title: " + videoInfo.getTitle());
            return false;
        }
        // endregion

        return true;
    }

    public ServiceResponse<List<VideoInfo>> getVideoInfoOfCurrentUser(@Valid PaginationParam paginationParam)
    {
        long pageCapacity = paginationParam.getPageCapacity();

        GetVideoInfosByUserIdParam queryParam = new GetVideoInfosByUserIdParam();
        queryParam.setUserId(UserContextService.getCurrentUser().getId());
        queryParam.setPageCapacity(pageCapacity);
        queryParam.setOffset(pageCapacity * (paginationParam.getPageIndex() - 1));

        List<VideoInfo> videoInfos = userVideoInfoMapper.getVideoInfosByUserId(queryParam);
        ServiceResponse<List<VideoInfo>> response = ServiceResponse.buildSuccessResponse(videoInfos);
        response.putExtra("size", videoInfos.size());

        return response;
    }

    public ServiceResponse<Long> countVideoByUserId()
    {
        Long count = userVideoInfoMapper.countVideoByUserId(UserContextService.getCurrentUser().getId());
        return ServiceResponse.buildSuccessResponse(count);
    }
}
