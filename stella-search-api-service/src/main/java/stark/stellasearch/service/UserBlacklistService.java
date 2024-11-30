package stark.stellasearch.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stark.dataworks.boot.web.PaginatedData;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.dao.AccountBaseInfoMapper;
import stark.stellasearch.dao.UserBlacklistMapper;
import stark.stellasearch.domain.UserBlacklist;
import stark.stellasearch.dto.params.*;
import stark.stellasearch.dto.results.UserBlacklistInfo;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class UserBlacklistService
{
    private final String FLAG_FOR_BLOCK = "blockUser";
    private final String FLAG_FOR_UNBLOCK = "unblockUser";
    private final String FLAG_FOR_CHECK = "checkUser";

    @Autowired
    private UserBlacklistMapper userBlacklistMapper;

    @Autowired
    private AccountBaseInfoMapper accountBaseInfoMapper;

    // TODO: Check if the record is in the table of user_blacklist when sending messages.
    public ServiceResponse<Long> blockUserByUsername(@Valid BlockUserRequest request)
    {
        // 1. Validate if the blocked user is the current user.
        // 2. Validate if the blocked user exists.
        // 3. Validate if the blocked user is already in the blacklist.

        String currentUsername = UserContextService.getCurrentUser().getUsername();
        String blockedUsername = request.getUsername();
        long currentUserId = UserContextService.getCurrentUser().getId();
        long blockedUserId;

        Long validateResult = validateUser(blockedUsername, currentUsername, currentUserId, FLAG_FOR_BLOCK);
        if (validateResult == -1)
            return ServiceResponse.buildErrorResponse(-1, "You can not block yourself.");
        else if (validateResult == -2)
            return ServiceResponse.buildErrorResponse(-2, "Invalid blocked user: " + blockedUsername + ", because it do not exist.");
        else if (validateResult == -3)
            return ServiceResponse.buildErrorResponse(-3, "The user is already blocked.");
        else
            blockedUserId = validateResult;

        // 4. Add the blocked user to the blacklist.
        UserBlacklist userBlacklist = insertUserToBlacklist(currentUserId, blockedUserId);
        Long userBlacklistId = userBlacklist.getId();

        return ServiceResponse.buildSuccessResponse(userBlacklistId);
    }

    public UserBlacklist insertUserToBlacklist(long currentUserId, long blockedUserId)
    {
        Date now = new Date();
        UserBlacklist userBlacklist = new UserBlacklist();
        userBlacklist.setUserId(currentUserId);
        userBlacklist.setBlockedUserId(blockedUserId);
        userBlacklist.setCreatorId(currentUserId);
        userBlacklist.setCreationTime(now);
        userBlacklist.setModifierId(currentUserId);
        userBlacklist.setModificationTime(now);
        userBlacklistMapper.insert(userBlacklist);

        return userBlacklist;
    }

    private Long validateUser(String blockedUsername, String currentUsername, long currentUserId, String flag)
    {
        if (currentUsername.equals(blockedUsername))
            return (long) -1;

        Long userId = accountBaseInfoMapper.getIdByUsername(blockedUsername);
        if (userId == null)
            return (long) -2;

        int userBlacklistCount = userBlacklistMapper.countByUserIds(currentUserId, userId);
        if (flag.equals(FLAG_FOR_CHECK))
            return (long) userBlacklistCount;
        if (flag.equals(FLAG_FOR_BLOCK) && userBlacklistCount != 0)
            return (long) -3;
        if (flag.equals(FLAG_FOR_UNBLOCK) && userBlacklistCount == 0)
            return (long) -4;

        return userId;
    }

    public ServiceResponse<Boolean> unblockUserByUsername(@Valid UnblockUserRequest request)
    {
        // 1. Validate if the unblocked user is the current user.
        // 2. Validate if the unblocked user exists.
        // 3. Validate if the unblocked user is in the blacklist.

        String currentUsername = UserContextService.getCurrentUser().getUsername();
        String unblockedUsername = request.getUsername();
        long currentUserId = UserContextService.getCurrentUser().getId();
        long unblockedUserId;

        Long validateResult = validateUser(unblockedUsername, currentUsername, currentUserId, FLAG_FOR_UNBLOCK);
        if (validateResult == -1)
            return ServiceResponse.buildErrorResponse(-1, "You can not unblock yourself.");
        else if (validateResult == -2)
            return ServiceResponse.buildErrorResponse(-2, "Invalid unblocked user: " + unblockedUsername + ", because it do not exist.");
        else if (validateResult == -4)
            return ServiceResponse.buildErrorResponse(-4, "The user is already unblocked.");
        else
            unblockedUserId = validateResult;

        // 3. Delete the blocked user from the blacklist.
        userBlacklistMapper.deleteByUserIds(currentUserId, unblockedUserId);

        return ServiceResponse.buildSuccessResponse(true);
    }

    public ServiceResponse<PaginatedData<UserBlacklistInfo>> getBlacklistOfCurrentUser(@Valid GetBlacklistRequest request)
    {
        long currentUserId = UserContextService.getCurrentUser().getId();
        GetUserBlacklistQueryParam queryParam = new GetUserBlacklistQueryParam();
        queryParam.setPaginationParam(request);
        queryParam.setCurrentUserId(currentUserId);

        List<UserBlacklistInfo> blacklistOfCurrentUser = userBlacklistMapper.getByUserIds(queryParam);
        long totalBlockedUsers = userBlacklistMapper.countBlockedUsersByUserId(currentUserId);
        PaginatedData<UserBlacklistInfo> paginatedData = new PaginatedData<>();
        paginatedData.setData(blacklistOfCurrentUser);
        paginatedData.setTotal(totalBlockedUsers);
        ServiceResponse<PaginatedData<UserBlacklistInfo>> response = ServiceResponse.buildSuccessResponse(paginatedData);
        response.putExtra("size", blacklistOfCurrentUser.size());

        return response;
    }

    public ServiceResponse<Boolean> ifUserBlocked(@Valid BlockUserRequest request)
    {
        String currentUsername = UserContextService.getCurrentUser().getUsername();
        String checkedUsername = request.getUsername();
        long currentUserId = UserContextService.getCurrentUser().getId();

        Long validateResult = validateUser(checkedUsername, currentUsername, currentUserId, FLAG_FOR_CHECK);
        if (validateResult == -1)
            return ServiceResponse.buildErrorResponse(-1, "You can not check yourself whether blocked or unblocked.");
        else if (validateResult == -2)
            return ServiceResponse.buildErrorResponse(-2, "Invalid unblocked user: " + checkedUsername + ", because it do not exist.");
        else if (validateResult == 0)
            return ServiceResponse.buildSuccessResponse(false);
        else
            return ServiceResponse.buildSuccessResponse(true);
    }
}
