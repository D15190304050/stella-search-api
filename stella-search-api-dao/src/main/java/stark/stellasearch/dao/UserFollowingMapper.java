package stark.stellasearch.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import stark.stellasearch.domain.UserFollowing;
import stark.stellasearch.dto.params.GetUserFollowingListQueryParam;
import stark.stellasearch.dto.results.UserFollowingInfo;

import java.util.List;

@Mapper
public interface UserFollowingMapper
{
    UserFollowing getByUserIdAndFollowedUserId(@Param("userId") long userId, @Param("followedUserId") long followedUserId);
    int insert(UserFollowing userFollowing);
    int deleteByUserIdAndFollowedUserId(@Param("userId") long userId, @Param("followedUserId") long followedUserId);
    List<UserFollowingInfo> getFollowings(GetUserFollowingListQueryParam queryParam);
    long countFollowingsByUserId(long userId);
    List<UserFollowingInfo> getFollowers(GetUserFollowingListQueryParam queryParam);
    long countFollowersByUserId(long userId);
    long countByUserIdAndFollowedUserId(@Param("currentUserId") long currentUserId, @Param("followedUserId") long followedUserId);
    long countUserFollowingsByUserIds(@Param("userId") long userId, @Param("followedUserId") long followedUserId);
}
