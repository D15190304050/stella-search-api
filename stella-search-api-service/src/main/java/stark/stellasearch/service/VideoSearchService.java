package stark.stellasearch.service;

import io.minio.errors.*;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import stark.dataworks.basic.data.redis.RedisQuickOperation;
import stark.dataworks.boot.autoconfig.minio.EasyMinio;
import stark.dataworks.boot.autoconfig.web.LogArgumentsAndResponse;
import stark.dataworks.boot.web.PaginatedData;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.dao.UserVideoInfoMapper;
import stark.stellasearch.dao.es.queryers.VideoSummaryInfoQueryer;
import stark.stellasearch.domain.entities.es.VideoSummaryInfo;
import stark.stellasearch.dto.params.SearchVideoQueryParam;
import stark.stellasearch.dto.params.SearchVideoRequest;
import stark.stellasearch.dto.results.ElasticsearchResult;
import stark.stellasearch.dto.results.VideoPlayInfo;
import stark.stellasearch.service.redis.RedisKeyManager;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@LogArgumentsAndResponse
@Validated
public class VideoSearchService
{
    @Value("${dataworks.easy-minio.bucket-name-videos}")
    private String bucketNameVideos;

    @Autowired
    private VideoSummaryInfoQueryer videoSummaryInfoQueryer;

    @Autowired
    private UserVideoInfoMapper userVideoInfoMapper;

    @Autowired
    private EasyMinio easyMinio;

    @Autowired
    private RedisQuickOperation redisQuickOperation;

    @Autowired
    private RedisKeyManager redisKeyManager;

    @Autowired
    @Qualifier("highPriorityTaskExecutor")
    private ThreadPoolTaskExecutor highPriorityTaskExecutor;

    public ServiceResponse<PaginatedData<VideoPlayInfo>> searchVideo(@Valid SearchVideoRequest request) throws IOException, InterruptedException
    {
        SearchVideoQueryParam queryParam = new SearchVideoQueryParam();
        queryParam.setKeyword(request.getKeyword());
        queryParam.setPaginationParam(request);

        ElasticsearchResult<VideoSummaryInfo> searchResult = videoSummaryInfoQueryer.searchVideo(queryParam);
        List<VideoSummaryInfo> summaryInfos = searchResult.getData();

        if (CollectionUtils.isEmpty(summaryInfos))
            return emptySearchResult(searchResult);

        long userId = UserContextService.getCurrentUser().getId();
        List<Long> videoIds = summaryInfos.stream().map(VideoSummaryInfo::getVideoId).toList();
        List<VideoPlayInfo> videoPlayInfos = userVideoInfoMapper.getVideoPlayInfosByIds(videoIds, userId);

        // There is no need to get video play URL here because frontend will request for video play URL
        // when entering video play page.
//        CountDownLatch latch = getVideoPlayUrls(videoPlayInfos);
        setSummaryInfo(videoPlayInfos, summaryInfos);

        PaginatedData<VideoPlayInfo> paginatedData = new PaginatedData<>();
        paginatedData.setData(videoPlayInfos);
        paginatedData.setTotal(searchResult.getTotal());

        ServiceResponse<PaginatedData<VideoPlayInfo>> response = ServiceResponse.buildSuccessResponse(paginatedData);
        response.putExtra("size", videoPlayInfos.size());

//        latch.await();
        return response;
    }

    @NotNull
    private static ServiceResponse<PaginatedData<VideoPlayInfo>> emptySearchResult(ElasticsearchResult<VideoSummaryInfo> searchResult)
    {
        PaginatedData<VideoPlayInfo> paginatedData = new PaginatedData<>();
        paginatedData.setTotal(searchResult.getTotal());
        paginatedData.setData(new ArrayList<>());
        return ServiceResponse.buildSuccessResponse(paginatedData);
    }

    private static void setSummaryInfo(List<VideoPlayInfo> videoPlayInfos, List<VideoSummaryInfo> summaryInfos)
    {
        Map<Long, VideoSummaryInfo> summaryInfoMap = summaryInfos.stream().collect(Collectors.toMap(VideoSummaryInfo::getVideoId, x -> x));
        for (VideoPlayInfo videoPlayInfo : videoPlayInfos)
        {
            long videoId = videoPlayInfo.getId();
            VideoSummaryInfo videoSummaryInfo = summaryInfoMap.get(videoId);
            if (videoSummaryInfo != null)
            {
                videoPlayInfo.setLabels(videoSummaryInfo.getLabels());
                videoPlayInfo.setSummary(videoSummaryInfo.getSummary());
            }
        }
    }

    private CountDownLatch getVideoPlayUrls(List<VideoPlayInfo> videoPlayInfos)
    {
        CountDownLatch latch = new CountDownLatch(videoPlayInfos.size());

        for (VideoPlayInfo videoPlayInfo : videoPlayInfos)
        {
            CompletableFuture.runAsync(() ->
            {
                try
                {
                    String nameInOss = videoPlayInfo.getNameInOss();
                    long videoId = videoPlayInfo.getId();
                    String videoPlayUrlKey = redisKeyManager.getVideoPlayUrlKey(videoId);
                    String videoPlayUrl = redisQuickOperation.get(videoPlayUrlKey);

                    // TODO: We may need a distributed lock here to prevent concurrent query of same object urls.
                    if (videoPlayUrl == null)
                    {
                        videoPlayUrl = easyMinio.getObjectUrl(bucketNameVideos, nameInOss);
                        redisQuickOperation.set(videoPlayUrlKey, videoPlayUrl, 5, TimeUnit.MINUTES);
                    }

                    videoPlayInfo.setVideoPlayUrl(videoPlayUrl);
                }
                catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                       NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                       InternalException e)
                {
                    log.error("Error getting video play url", e);
                }
                finally
                {
                    latch.countDown();
                }
            }, highPriorityTaskExecutor);
        }

        return latch;
    }
}
