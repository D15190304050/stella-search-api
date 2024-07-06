package stark.stellasearch.service;

import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import stark.dataworks.basic.data.redis.RedisQuickOperation;
import stark.dataworks.boot.autoconfig.minio.EasyMinio;
import stark.dataworks.boot.autoconfig.web.LogArgumentsAndResponse;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.dto.params.ClearOldUploadingTaskRequest;
import stark.stellasearch.dto.params.ComposeVideoChunksRequest;
import stark.stellasearch.dto.params.NewVideoUploadingTaskRequest;
import stark.stellasearch.dto.params.VideoChunkUploadingRequest;
import stark.stellasearch.service.dto.User;

import javax.validation.Valid;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
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

    @Autowired
    private RedisQuickOperation redisQuickOperation;

    @Autowired
    private EasyMinio easyMinio;

    @Autowired
    @Qualifier("lowPriorityTaskExecutor")
    private ThreadPoolTaskExecutor lowPriorityTaskExecutor;

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
    public ServiceResponse<Boolean> composeVideoChunks(@Valid ComposeVideoChunksRequest request) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
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
        clearUploadingTask(taskId);

        // Remove chunks in MinIO (compose operation will only create a new object without any changes on existing objects.)
        easyMinio.deleteObjects(bucketNameVideos, sortedChunkNames);

        return ServiceResponse.buildSuccessResponse(true);
    }

    private void resetExpirationOfUploadingTask(String taskId)
    {
        redisQuickOperation.expire(taskId, 30, TimeUnit.MINUTES);
    }

    public ServiceResponse<Boolean> clearOldUploadingTask(@Valid ClearOldUploadingTaskRequest request)
    {
        String taskId = request.getVideoUploadingTaskId();

        lowPriorityTaskExecutor.execute(() -> clearUploadingTask(taskId));

        return ServiceResponse.buildSuccessResponse(true);
    }

    /**
     * Clear the resources of the video uploading task, which will not be used again.
     * The resources contain keys in redis, a record in database, and chunks in MinIO.
     * @param taskId
     */
    private void clearUploadingTask(String taskId)
    {
        // TODO: Clear the database record.

        String chunkCountKey = generateTaskChunkCountKey(taskId);
        String videoFileExtensionKey = taskId + VIDEO_FILE_EXTENSION;

        redisQuickOperation.delete(taskId);
        redisQuickOperation.delete(chunkCountKey);
        redisQuickOperation.delete(videoFileExtensionKey);
    }
}
