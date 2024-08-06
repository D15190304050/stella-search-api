package stark.stellasearch.dao;

import org.apache.ibatis.annotations.Mapper;
import stark.stellasearch.domain.UserVideoInfo;

@Mapper
public interface UserVideoInfoMapper
{
    int insert(UserVideoInfo userVideoInfo);
    UserVideoInfo getVideoInfoById(long id);
    int updateVideoInfoById(UserVideoInfo userVideoInfo);
}
