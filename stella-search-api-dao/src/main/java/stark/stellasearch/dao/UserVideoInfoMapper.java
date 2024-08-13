package stark.stellasearch.dao;

import org.apache.ibatis.annotations.Mapper;
import stark.stellasearch.domain.UserVideoInfo;
import stark.stellasearch.dto.params.GetVideoInfosByUserIdParam;
import stark.stellasearch.dto.results.VideoInfo;

import java.util.List;

@Mapper
public interface UserVideoInfoMapper
{
    int insert(UserVideoInfo userVideoInfo);
    UserVideoInfo getVideoInfoById(long id);
    int updateVideoInfoById(UserVideoInfo userVideoInfo);
    List<VideoInfo> getVideoInfosByUserId(GetVideoInfosByUserIdParam getVideoInfosByUserIdParam);
    long countVideoByUserId(long userId);
    UserVideoInfo getVideoBaseInfoById(long id);
    long countVideoInfoByUrl(String videoUrl);
}
