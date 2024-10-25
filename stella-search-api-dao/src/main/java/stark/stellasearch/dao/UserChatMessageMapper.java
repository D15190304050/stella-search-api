package stark.stellasearch.dao;

import org.apache.ibatis.annotations.Mapper;
import stark.stellasearch.domain.UserChatMessage;
import stark.stellasearch.dto.params.GetUserChatMessageQueryParam;

import java.util.List;

@Mapper
public interface UserChatMessageMapper
{
    int insert(UserChatMessage userChatMessage);
    List<UserChatMessage> getMessagesBySessionId(GetUserChatMessageQueryParam queryParam);
    long countMessagesBySessionId(long sessionId);
}
