package stark.stellasearch.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import stark.dataworks.boot.web.PaginatedData;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.domain.UserChatMessage;
import stark.stellasearch.domain.UserChatSession;
import stark.stellasearch.dto.params.*;
import stark.stellasearch.dto.results.UserChatSessionInfo;
import stark.stellasearch.service.UserChatMessageService;
import stark.stellasearch.service.UserChatSessionService;

@Controller
@ResponseBody
@RequestMapping("/user-chat")
public class UserChatController
{
    // TODO:
    // 1. Create a new chat session.
    // 2. Get chat session info with the last message.
    // 3. Get all chat session info from a user with the last message.
    // 4. Delete a chat session.

    // 5. Send messages.
    // 6. Get all messages from a chat.

    @Autowired
    private UserChatSessionService userChatSessionService;

    @Autowired
    private UserChatMessageService userChatMessageService;

    @PostMapping("session/create")
    public ServiceResponse<UserChatSession> createChatSession(@RequestBody CreateUserChatSessionRequest request)
    {
        return userChatSessionService.createChatSession(request);
    }

    @GetMapping("session/{sessionId}")
    public ServiceResponse<UserChatSessionInfo> getChatSession(@PathVariable("sessionId") long sessionId)
    {
        return userChatSessionService.getChatSession(sessionId);
    }

    @GetMapping("session/list")
    public ServiceResponse<PaginatedData<UserChatSessionInfo>> getChatSessionList(@ModelAttribute GetUserChatSessionListRequest request)
    {
        return userChatSessionService.getChatSessionList(request);
    }

    @PostMapping("session/delete")
    public ServiceResponse<Boolean> deleteChatSession(@RequestBody DeleteUserChatSessionRequest request)
    {
        return userChatSessionService.deleteChatSession(request);
    }

    @PostMapping("/message/send")
    public ServiceResponse<Long> sendMessage(@RequestBody SendUserChatMessageRequest request)
    {
        return userChatMessageService.sendMessages(request);
    }

    @GetMapping("message/{sessionId}")
    public ServiceResponse<PaginatedData<UserChatMessage>> getMessages(@PathVariable("sessionId") long sessionId, PaginationRequestParam paginationRequestParam)
    {
        return userChatMessageService.getMessagesBySessionId(sessionId, paginationRequestParam);
    }

}
