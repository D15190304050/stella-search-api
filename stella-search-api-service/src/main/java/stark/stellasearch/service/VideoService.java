package stark.stellasearch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import stark.dataworks.basic.data.redis.RedisQuickOperation;
import stark.dataworks.boot.autoconfig.minio.EasyMinio;
import stark.dataworks.boot.autoconfig.web.LogArgumentsAndResponse;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.dto.params.NewVideoUploadingTaskRequest;
import stark.stellasearch.dto.params.VideoChunkUploadingRequest;
import stark.stellasearch.service.dto.User;
import stark.stellasearch.service.helpers.VideoUploadingHelper;

import javax.validation.Valid;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@LogArgumentsAndResponse
@Validated
public class VideoService
{
    @Autowired
    private RedisQuickOperation redisQuickOperation;

    @Autowired
    private EasyMinio easyMinio;

    public ServiceResponse<String> generateNewVideoUploadingTask(@Valid NewVideoUploadingTaskRequest request)
    {
        String taskIdPrefix = VideoUploadingHelper.getVideoUploadingTaskIdPrefix(request.getVideoName());

        String taskId;
        do
            taskId = taskIdPrefix + "-" + UUID.randomUUID();
        while (!redisQuickOperation.containsKey(taskId));

        redisQuickOperation.set(taskId, taskIdPrefix, 60, TimeUnit.MINUTES);

        return ServiceResponse.buildSuccessResponse(taskId);
    }

    public ServiceResponse<Boolean> uploadVideoChunk(@Valid VideoChunkUploadingRequest request)
    {


        return null;
    }
}
