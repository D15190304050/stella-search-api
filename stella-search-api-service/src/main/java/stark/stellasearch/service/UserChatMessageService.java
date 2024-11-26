package stark.stellasearch.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stark.dataworks.boot.web.PaginatedData;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.dao.AccountBaseInfoMapper;
import stark.stellasearch.dao.UserChatMessageMapper;
import stark.stellasearch.dao.UserChatSessionMapper;
import stark.stellasearch.domain.UserChatMessage;
import stark.stellasearch.dto.params.GetUserChatMessageQueryParam;
import stark.stellasearch.dto.params.PaginationRequestParam;
import stark.stellasearch.dto.params.SendUserChatMessageRequest;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class UserChatMessageService
{
    @Autowired
    private AccountBaseInfoMapper accountBaseInfoMapper;

    @Autowired
    private UserChatMessageMapper userChatMessageMapper;

    @Autowired
    private UserChatSessionMapper userChatSessionMapper;

    public ServiceResponse<Long> sendMessages(@Valid SendUserChatMessageRequest request)
    {
        // 1. Validate if the recipient id exists.
        long recipientId = request.getRecipientId();
        long currentUserId = UserContextService.getCurrentUser().getId();
        if (accountBaseInfoMapper.countByUserId(recipientId) == 0)
            return ServiceResponse.buildErrorResponse(-1, "The recipient does not exist.");

        // 2. Validate if the session id exists.
        long sessionId = userChatSessionMapper.getSessionIdByUserIds(currentUserId, recipientId);
        if (sessionId <= 0)
            return ServiceResponse.buildErrorResponse(-2, "User session does not exist.");

        // 3. Send message.
        UserChatMessage userChatMessage = getUserChatMessage(request, currentUserId, sessionId);

        // 4. Return the message id.
        return ServiceResponse.buildSuccessResponse(userChatMessage.getId());
    }

    private UserChatMessage getUserChatMessage(SendUserChatMessageRequest request, long currentUserId, long sessionId)
    {
        Date now = new Date();
        UserChatMessage userChatMessage = new UserChatMessage();
        userChatMessage.setSenderId(currentUserId);
        userChatMessage.setRecipientId(request.getRecipientId());
        userChatMessage.setContent(request.getContent());
        userChatMessage.setSessionId(sessionId);
        userChatMessage.setCreationTime(now);
        userChatMessage.setCreatorId(currentUserId);
        userChatMessage.setModificationTime(now);
        userChatMessage.setModifierId(currentUserId);
        userChatMessageMapper.insert(userChatMessage);
        return userChatMessage;
    }

    public ServiceResponse<PaginatedData<UserChatMessage>> getMessagesBySessionId(long sessionId, PaginationRequestParam paginationParam)
    {
        // 1. Validate if the session id exists.
        if (sessionId <= 0)
            return ServiceResponse.buildErrorResponse(-1, "Invalid session id.");

        if (userChatSessionMapper.countById(sessionId) == 0)
            return ServiceResponse.buildErrorResponse(-2, "User session does not exist.");

        // 2. Get messages from session id.
        GetUserChatMessageQueryParam queryParam = new GetUserChatMessageQueryParam();
        queryParam.setSessionId(sessionId);
        queryParam.setPaginationParam(paginationParam);

        List<UserChatMessage> paginatedMessages = userChatMessageMapper.getMessagesBySessionId(queryParam);
        long count = userChatMessageMapper.countMessagesBySessionId(sessionId);

        PaginatedData<UserChatMessage> paginatedData = new PaginatedData<>();
        paginatedData.setTotal(count);
        paginatedData.setData(paginatedMessages);

        ServiceResponse<PaginatedData<UserChatMessage>> response = ServiceResponse.buildSuccessResponse(paginatedData);
        response.putExtra("size", paginatedMessages.size());
        return response;
    }
}
