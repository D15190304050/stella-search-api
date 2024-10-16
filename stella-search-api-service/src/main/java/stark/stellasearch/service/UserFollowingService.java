package stark.stellasearch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.dao.AccountBaseInfoMapper;
import stark.stellasearch.dao.UserFollowingMapper;
import stark.stellasearch.domain.AccountBaseInfo;
import stark.stellasearch.domain.UserFollowing;
import stark.stellasearch.dto.params.UserFollowingRequest;
import stark.stellasearch.dto.params.UserUnfollowingRequest;
import stark.stellasearch.service.dto.User;

import javax.validation.Valid;
import java.util.Date;

@Slf4j
@Service
public class UserFollowingService
{
    private static final int FOLLOW_STATUS_FOLLOWING = 1;

    @Autowired
    private AccountBaseInfoMapper accountBaseInfoMapper;

    @Autowired
    private UserFollowingMapper userFollowingMapper;

    public ServiceResponse<Boolean> follow(@Valid UserFollowingRequest userFollowingRequest)
    {
        // 1. Validate if the username is not the same as the current user.
        User currentUser = UserContextService.getCurrentUser();
        long currentUserId = currentUser.getId();
        String followingUsername = userFollowingRequest.getUsername();
        if (currentUser.getUsername().equals(followingUsername))
            return ServiceResponse.buildErrorResponse(-1, "You cannot follow yourself.");

        // 2. Validate if the username exists.
        AccountBaseInfo followingAccount = accountBaseInfoMapper.getAccountByUsername(followingUsername);
        if (followingAccount == null)
            return ServiceResponse.buildErrorResponse(-1, "The username does not exist.");

        // 3. Validate if the user is already following the user.
        UserFollowing userFollowingRecord = userFollowingMapper.getByUserIDAndFollowedUserID(currentUserId, followingAccount.getId());
        if (userFollowingRecord != null)
            return ServiceResponse.buildErrorResponse(-1, "You have already followed the user.");


        // 4. Insert into the user_following table.
        Date now = new Date();
        UserFollowing userFollowingInfo = new UserFollowing();
        userFollowingInfo.setUserId(currentUserId);
        userFollowingInfo.setFollowedUserId(followingAccount.getId());
        userFollowingInfo.setFollowingStatus(FOLLOW_STATUS_FOLLOWING);
        userFollowingInfo.setCreatorId(currentUserId);
        userFollowingInfo.setCreationTime(now);
        userFollowingInfo.setModificationTime(now);
        userFollowingMapper.insert(userFollowingInfo);

        return ServiceResponse.buildSuccessResponse(true);
    }

    public ServiceResponse<Boolean> unfollow(@Valid UserUnfollowingRequest userUnfollowingRequest)
    {
        // 1. Validate if the username is not the same as the current user.
        User currentUser = UserContextService.getCurrentUser();
        long currentUserId = currentUser.getId();
        String followingUsername = userUnfollowingRequest.getUsername();
        if (currentUser.getUsername().equals(followingUsername))
            return ServiceResponse.buildErrorResponse(-1, "You cannot unfollow yourself.");

        // 2. Validate if the username exists.
        AccountBaseInfo followingAccount = accountBaseInfoMapper.getAccountByUsername(followingUsername);
        if (followingAccount == null)
            return ServiceResponse.buildErrorResponse(-1, "The username does not exist.");

        // 3. Delete from the user_following table.
        userFollowingMapper.deleteByUserIdAndFollowedUserId(currentUserId, followingAccount.getId());
        return ServiceResponse.buildSuccessResponse(true);
    }

}
