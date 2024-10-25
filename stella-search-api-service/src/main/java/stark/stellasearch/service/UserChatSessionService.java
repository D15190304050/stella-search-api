package stark.stellasearch.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stark.dataworks.boot.web.PaginatedData;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.dao.AccountBaseInfoMapper;
import stark.stellasearch.dao.UserChatSessionMapper;
import stark.stellasearch.domain.AccountBaseInfo;
import stark.stellasearch.domain.UserChatSession;
import stark.stellasearch.dto.params.*;
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

    public ServiceResponse<UserChatSession> createChatSession(@Valid CreateUserChatSessionRequest request)
    {
        // 1. Validate if the recipient id exists.
        AccountBaseInfo recipientInfo = accountBaseInfoMapper.getAccountByUsername(request.getRecipientName());
        if (recipientInfo == null)
            return ServiceResponse.buildErrorResponse(-1, "The recipient does not exist.");

        // 2. Create chat session.
        long currentUserId = UserContextService.getCurrentUser().getId();
        Date now = new java.util.Date();
        UserChatSession newChatSession = new UserChatSession();
        newChatSession.setUser1Id(currentUserId);
        newChatSession.setUser2Id(recipientInfo.getId());
        newChatSession.setStatus(1);
        newChatSession.setCreatorId(currentUserId);
        newChatSession.setCreationTime(now);
        newChatSession.setModifierId(currentUserId);
        newChatSession.setModificationTime(now);

        userChatSessionMapper.insert(newChatSession);

        return ServiceResponse.buildSuccessResponse(newChatSession);
    }

    public ServiceResponse<UserChatSessionInfo> getChatSession(long sessionId)
    {
        if (sessionId <= 0)
            return ServiceResponse.buildErrorResponse(-1, "Invalid session id.");

        UserChatSessionInfo chatSession = userChatSessionMapper.getSessionInfoWithLastMessageById(sessionId);

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

    public ServiceResponse<Boolean> deleteChatSession(@Valid DeleteUserChatSessionRequest request)
    {
        // 1. Validate if the session id exists.
        long sessionId = request.getSessionId();
        if (userChatSessionMapper.getSessionInfoWithLastMessageById(sessionId) == null)
            return ServiceResponse.buildErrorResponse(-1, "Invalid session id.");

        // 2. Delete the user chat session.
        if (userChatSessionMapper.deleteById(sessionId) != 1)
            return ServiceResponse.buildErrorResponse(-2, "Failed to delete session.");

        return ServiceResponse.buildSuccessResponse(true);
    }
}
