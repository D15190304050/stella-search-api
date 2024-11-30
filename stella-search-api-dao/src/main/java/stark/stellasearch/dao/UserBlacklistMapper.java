package stark.stellasearch.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import stark.stellasearch.domain.UserBlacklist;
import stark.stellasearch.dto.params.GetUserBlacklistQueryParam;
import stark.stellasearch.dto.results.UserBlacklistInfo;

import java.util.List;

@Mapper
public interface UserBlacklistMapper
{
    int countByUserIds(@Param("userId") long userId, @Param("blockedUserId") long blockedUserId);
    int insert(UserBlacklist userBlacklist);
    int deleteByUserIds(@Param("userId") long userId, @Param("blockedUserId") long blockedUserId);
    List<UserBlacklistInfo> getByUserIds(GetUserBlacklistQueryParam param);
    long countBlockedUsersByUserId(@Param("userId") long userId);

}
