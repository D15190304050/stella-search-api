package stark.stellasearch.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.dao.AccountBaseInfoMapper;
import stark.stellasearch.dao.UserBlacklistMapper;
import stark.stellasearch.domain.UserBlacklist;
import stark.stellasearch.dto.params.BlockUserRequest;

import java.util.Date;

@Slf4j
@Service
public class UserBlacklistService
{
    @Autowired
    private UserBlacklistMapper userBlacklistMapper;

    @Autowired
    private AccountBaseInfoMapper accountBaseInfoMapper;

    // TODO: Check if the record is in the table of user_blacklist when sending messages.
    public ServiceResponse<Boolean> blockUserByUsername(@Valid BlockUserRequest request)
    {
        // 1. Validate if the blocked user is the current user.
        String currentUsername = UserContextService.getCurrentUser().getUsername();
        String blockedUsername = request.getUsername();
        if (currentUsername.equals(blockedUsername))
            return ServiceResponse.buildErrorResponse(-1, "You can not block yourself.");

        // 2. Validate if the blocked user exists.
        Long blockedUserId = accountBaseInfoMapper.getUserIdByUsername(blockedUsername);
        if (blockedUserId == null)
            return ServiceResponse.buildErrorResponse(-2, "Invalid blocked user: " + blockedUsername);

        // 3. Validate if the blocked user is already in the blacklist.
        long currentUserId = UserContextService.getCurrentUser().getId();
        if (userBlacklistMapper.countByUserIds(currentUserId, blockedUserId) == 0)
            return ServiceResponse.buildErrorResponse(-3, "The user is already blocked.");

        // 4. Add the blocked user to the blacklist.
        Date now = new Date();
        UserBlacklist userBlacklist = new UserBlacklist();
        userBlacklist.setUserId(currentUserId);
        userBlacklist.setBlockedUserId(blockedUserId);
        userBlacklist.setCreatorId(currentUserId);
        userBlacklist.setCreationTime(now);
        userBlacklist.setModifierId(currentUserId);
        userBlacklist.setModificationTime(now);
        userBlacklistMapper.insert(userBlacklist);

        return ServiceResponse.buildSuccessResponse(true);
    }
}
