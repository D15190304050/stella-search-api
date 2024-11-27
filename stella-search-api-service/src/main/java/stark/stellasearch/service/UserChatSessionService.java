package stark.stellasearch.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import stark.dataworks.boot.web.PaginatedData;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.dao.AccountBaseInfoMapper;
import stark.stellasearch.dao.UserChatMessageMapper;
import stark.stellasearch.dao.UserChatSessionMapper;
import stark.stellasearch.domain.AccountBaseInfo;
import stark.stellasearch.domain.UserChatSession;
import stark.stellasearch.dto.params.CreateUserChatSessionRequest;
import stark.stellasearch.dto.params.DeleteUserChatSessionRequest;
import stark.stellasearch.dto.params.GetUserChatSessionListQueryParam;
import stark.stellasearch.dto.params.GetUserChatSessionListRequest;
import stark.stellasearch.dto.results.UserChatSessionInfo;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class UserChatSessionService
{
    @Autowired
    private UserChatSessionMapper userChatSessionMapper;

    @Autowired
    private AccountBaseInfoMapper accountBaseInfoMapper;
    @Autowired
    private UserChatMessageMapper userChatMessageMapper;

    public ServiceResponse<UserChatSession> createChatSession(@Valid CreateUserChatSessionRequest request)
    {
        // 1. Validate if the recipient name equals current user name.
        String recipientName = request.getRecipientName();
        String currentUsername = UserContextService.getCurrentUser().getUsername();
        long currentUserId = UserContextService.getCurrentUser().getId();
        if (recipientName.equals(currentUsername))
            return ServiceResponse.buildErrorResponse(-1, "You cannot create a chat session with yourself.");

        // 2. Validate if the recipient name exists.
        Long recipientId = accountBaseInfoMapper.getIdByUsername(recipientName);
        if (recipientId == null)
            return ServiceResponse.buildErrorResponse(-1, "The recipient does not exist.");

        // 3. Validate if the session already exists.
        if (userChatSessionMapper.getSessionByUserIds(recipientId, currentUserId) != null)
            return ServiceResponse.buildErrorResponse(-2, "The chat session already exists.");

        // 3. Create chat session.
        UserChatSession newChatSession = getUserChatSession(recipientId, currentUserId);

        userChatSessionMapper.insert(newChatSession);

        return ServiceResponse.buildSuccessResponse(newChatSession);
    }

    private static UserChatSession getUserChatSession(Long recipientId, long currentUserId)
    {
        Date now = new Date();
        UserChatSession newChatSession = new UserChatSession();
        newChatSession.setUser1Id(currentUserId);
        newChatSession.setUser2Id(recipientId);
        newChatSession.setState(1);
        newChatSession.setCreatorId(currentUserId);
        newChatSession.setCreationTime(now);
        newChatSession.setModifierId(currentUserId);
        newChatSession.setModificationTime(now);
        return newChatSession;
    }

    public ServiceResponse<UserChatSessionInfo> getChatSession(long sessionId)
    {
        if (sessionId <= 0)
            return ServiceResponse.buildErrorResponse(-1, "Invalid session id.");

        UserChatSessionInfo chatSession = userChatSessionMapper.getSessionInfoWithLastMessageById(sessionId);
        if (chatSession == null)
            return ServiceResponse.buildErrorResponse(-1, "Session does not exist.");

        return ServiceResponse.buildSuccessResponse(chatSession);
    }

    public ServiceResponse<PaginatedData<UserChatSessionInfo>> getChatSessionList(@Valid GetUserChatSessionListRequest request)
    {
        long currentUserId = UserContextService.getCurrentUser().getId();
        GetUserChatSessionListQueryParam queryParam = new GetUserChatSessionListQueryParam();
        queryParam.setUserId(currentUserId);
        queryParam.setPaginationParam(request);

        List<UserChatSessionInfo> chatSessionList = userChatSessionMapper.getAllSessionsByUserId(queryParam);
        long count = userChatSessionMapper.countAllSessionsByUserId(currentUserId);
        PaginatedData<UserChatSessionInfo> paginatedData = new PaginatedData<>();
        paginatedData.setData(chatSessionList);
        paginatedData.setTotal(count);

        ServiceResponse<PaginatedData<UserChatSessionInfo>> response = ServiceResponse.buildSuccessResponse(paginatedData);
        response.putExtra("size", chatSessionList.size());

        return response;
    }

    @Transactional(rollbackFor = Exception.class)
    public ServiceResponse<Boolean> deleteChatSession(@Valid DeleteUserChatSessionRequest request)
    {
        // 1. Validate if the session id exists.
        long sessionId = request.getSessionId();
        if (userChatSessionMapper.getSessionInfoWithLastMessageById(sessionId) == null)
            return ServiceResponse.buildErrorResponse(-1, "Invalid session id.");

        // 2. Delete the user chat session.
        if (userChatSessionMapper.deleteById(sessionId) != 1)
            return ServiceResponse.buildErrorResponse(-2, "Failed to delete session.");

        // 3. Delete the user chat messages.
        userChatMessageMapper.deleteMessagesBySessionId(sessionId);

        return ServiceResponse.buildSuccessResponse(true);
    }
}
