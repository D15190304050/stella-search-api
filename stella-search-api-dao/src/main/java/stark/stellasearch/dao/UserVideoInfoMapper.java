package stark.stellasearch.dao;

import org.apache.ibatis.annotations.Mapper;
import stark.stellasearch.domain.UserVideoInfo;
import stark.stellasearch.dto.params.GetVideoInfosByUserIdParam;
import stark.stellasearch.dto.results.VideoPlayInfo;

import java.util.List;

@Mapper
public interface UserVideoInfoMapper
{
    int insert(UserVideoInfo userVideoInfo);
    int updateVideoInfoById(UserVideoInfo userVideoInfo);
    List<VideoPlayInfo> getVideoPlayInfosByUserId(GetVideoInfosByUserIdParam getVideoInfosByUserIdParam);
    long countVideoByUserId(long userId);
    UserVideoInfo getVideoBaseInfoById(long id);
    VideoPlayInfo getVideoPlayInfoById(long videoId);
}
