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
import stark.stellasearch.domain.AccountBaseInfo;
import stark.stellasearch.domain.UserChatMessage;
import stark.stellasearch.domain.UserChatSession;
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
        AccountBaseInfo recipientInfo = accountBaseInfoMapper.getAccountByUserId(recipientId);
        if (recipientInfo == null)
            return ServiceResponse.buildErrorResponse(-1, "The recipient does not exist.");

        // 2. Validate if the session id exists.
        UserChatSession chatSession = userChatSessionMapper.getSessionByUserIds(currentUserId, recipientId);
        if (chatSession == null)
            return ServiceResponse.buildErrorResponse(-2, "User session does not exist.");

        // 3. Send message.
        Date now = new Date();
        UserChatMessage userChatMessage = new UserChatMessage();
        userChatMessage.setSenderId(currentUserId);
        userChatMessage.setRecipientId(recipientId);
        userChatMessage.setContent(request.getContent());
        userChatMessage.setSessionId(chatSession.getId());
        userChatMessage.setCreationTime(now);
        userChatMessage.setCreatorId(currentUserId);
        userChatMessage.setModificationTime(now);
        userChatMessage.setModifierId(currentUserId);
        userChatMessageMapper.insert(userChatMessage);

        // 4. Return the message id.
        return ServiceResponse.buildSuccessResponse(userChatMessage.getId());
    }

    public ServiceResponse<PaginatedData<UserChatMessage>> getMessagesBySessionId(long sessionId, PaginationRequestParam paginationParam)
    {
        // 1. Validate if the session id exists.
        if (sessionId <= 0)
            return ServiceResponse.buildErrorResponse(-1, "Invalid session id.");
        UserChatSession chatSession = userChatSessionMapper.getSessionById(sessionId);
        if (chatSession == null)
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
