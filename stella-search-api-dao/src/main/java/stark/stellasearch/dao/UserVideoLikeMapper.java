package stark.stellasearch.dao;

import org.apache.ibatis.annotations.Mapper;
import stark.stellasearch.domain.UserVideoLike;

@Mapper
public interface UserVideoLikeMapper
{
    long countUserVideoLike(long userId, long videoId);
    int insertLike(UserVideoLike userVideoLike);
    int deleteLike(long userId, long videoId);
}
