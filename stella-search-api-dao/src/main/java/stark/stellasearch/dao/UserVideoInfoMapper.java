package stark.stellasearch.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import stark.stellasearch.domain.UserVideoInfo;
import stark.stellasearch.dto.params.GetVideoInfosByKeywordQueryParam;
import stark.stellasearch.dto.params.GetVideoInfosByUserIdQueryParam;
import stark.stellasearch.dto.params.GetVideoPlayInfoInPlaylistQueryParam;
import stark.stellasearch.dto.results.VideoPlayInfo;

import java.util.List;

@Mapper
public interface UserVideoInfoMapper
{
    int insert(UserVideoInfo userVideoInfo);
    int updateVideoInfoById(UserVideoInfo userVideoInfo);
    List<VideoPlayInfo> getVideoPlayInfosByUserId(GetVideoInfosByUserIdQueryParam getVideoInfosByUserIdQueryParam);
    long countVideoByUserId(long userId);
    UserVideoInfo getVideoBaseInfoById(long id);
    VideoPlayInfo getVideoPlayInfoById(@Param("videoId") long videoId, @Param("userId") long userId);
    long countVideoById(long id);
    List<VideoPlayInfo> getVideoPlayInfosByKeyword(GetVideoInfosByKeywordQueryParam getVideoInfosByKeywordQueryParam);
    long countVideoByKeyword(String keyword);
    List<VideoPlayInfo> getVideoPlayInfosByPlaylistId(GetVideoPlayInfoInPlaylistQueryParam queryParam);
    long setVideoSummaryFileNameById(@Param("id") long id, @Param("summaryFileName") String summaryFileName);
}
