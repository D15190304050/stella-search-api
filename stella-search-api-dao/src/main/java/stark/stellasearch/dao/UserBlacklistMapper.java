package stark.stellasearch.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import stark.stellasearch.domain.UserBlacklist;

@Mapper
public interface UserBlacklistMapper
{
    int countByUserIds(@Param("userId") long userId, @Param("blockedUserId") long blockedUserId);
    int insert(UserBlacklist userBlacklist);
}
