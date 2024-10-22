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
    UserFollowing getByUserIDAndFollowedUserID(@Param("userId") long userId, @Param("followedUserId") long followedUserId);
    int insert(UserFollowing userFollowing);
    int deleteByUserIdAndFollowedUserId(@Param("userId") long userId, @Param("followedUserId") long followedUserId);
    List<UserFollowingInfo> getFollowingList(GetUserFollowingListQueryParam queryParam);
    long countFollowingUsersByUsername(String username);
    List<UserFollowingInfo> getFollowerList(GetUserFollowingListQueryParam queryParam);
    long countFollowersByUsername(String username);
    int countByUsernameAndFollowedUsername(@Param("ifFollowingUsername") String ifFollowingUsername, @Param("currentUsername") String currentUsername);
}
