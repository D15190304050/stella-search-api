package stark.stellasearch.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import stark.stellasearch.domain.UserChatSession;
import stark.stellasearch.dto.params.GetUserChatSessionListQueryParam;
import stark.stellasearch.dto.results.UserChatSessionInfo;

import java.util.List;

@Mapper
public interface UserChatSessionMapper
{
    int insert(UserChatSession userChatSession);
    UserChatSessionInfo getSessionInfoWithLastMessageById(long id);
    List<UserChatSessionInfo> getAllSessionsByUserId(GetUserChatSessionListQueryParam queryParam);
    int deleteById(long id);
    long countAllSessionsByUserId(long userId);
    UserChatSession getSessionByUserIds(@Param("user1Id") long user1Id, @Param("user2Id") long user2Id);
    int countById(long id);
    long getSessionIdByUserIds(@Param("user1Id") long user1Id, @Param("user2Id") long user2Id);
}
