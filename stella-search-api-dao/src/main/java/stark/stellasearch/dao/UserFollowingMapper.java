package stark.stellasearch.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import stark.stellasearch.domain.UserFollowing;

@Mapper
public interface UserFollowingMapper
{
    UserFollowing getByUserIDAndFollowedUserID(@Param("userId") long userId, @Param("followedUserId") long followedUserId);
    int insert(UserFollowing userFollowing);
    int deleteByUserIdAndFollowedUserId(@Param("userId") long userId, @Param("followedUserId") long followedUserId);
}
