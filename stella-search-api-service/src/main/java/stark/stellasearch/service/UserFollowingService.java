package stark.stellasearch.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stark.dataworks.boot.web.PaginatedData;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.dao.AccountBaseInfoMapper;
import stark.stellasearch.dao.UserFollowingMapper;
import stark.stellasearch.domain.AccountBaseInfo;
import stark.stellasearch.domain.UserFollowing;
import stark.stellasearch.dto.params.*;
import stark.stellasearch.dto.results.UserFollowingInfo;
import stark.stellasearch.service.dto.User;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class UserFollowingService
{
    private static final int FOLLOW_STATUS_FOLLOWING = 1;
    private static final String FOLLOWING_USER_LIST_FLAG ="followingUserList";
    private static final String FOLLOWER_LIST_FLAG ="followerList";

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

    public ServiceResponse<PaginatedData<UserFollowingInfo>> getFollowingUserOrFollowerList(@Valid GetUserFollowingListRequest request, String requestFlag)
    {
        // 1. Validate if the username exists.
        String username = request.getUsername();
        AccountBaseInfo followingAccount = accountBaseInfoMapper.getAccountByUsername(username);
        if (followingAccount == null)
            return ServiceResponse.buildErrorResponse(-1, "The username does not exist.");

        // 2. Get the following user or follower list.
        GetUserFollowingListQueryParam userFollowingListQueryParam = new GetUserFollowingListQueryParam();
        userFollowingListQueryParam.setUsername(username);
        userFollowingListQueryParam.setPaginationParam(request);

        List<UserFollowingInfo> userFollowingInfoList;
        long total;
        if (requestFlag.equals(FOLLOWING_USER_LIST_FLAG))
        {
            userFollowingInfoList = userFollowingMapper.getFollowingList(userFollowingListQueryParam);
            total = userFollowingMapper.countFollowingUsersByUsername(username);
        } else
        {
            userFollowingInfoList = userFollowingMapper.getFollowerList(userFollowingListQueryParam);
            total = userFollowingMapper.countFollowersByUsername(username);
        }

        PaginatedData<UserFollowingInfo> response = new PaginatedData<>();
        response.setData(userFollowingInfoList);
        response.setTotal(total);
        ServiceResponse<PaginatedData<UserFollowingInfo>> paginatedDataResponse = ServiceResponse.buildSuccessResponse(response);
        paginatedDataResponse.putExtra("size", userFollowingInfoList.size());

        return paginatedDataResponse;
    }

    public ServiceResponse<PaginatedData<UserFollowingInfo>> getFollowingList(@Valid GetUserFollowingListRequest request)
    {
        return getFollowingUserOrFollowerList(request, FOLLOWING_USER_LIST_FLAG);
    }

    public ServiceResponse<PaginatedData<UserFollowingInfo>> getFollowerList(@Valid GetUserFollowingListRequest request)
    {
        return getFollowingUserOrFollowerList(request, FOLLOWER_LIST_FLAG);
    }

    public ServiceResponse<Boolean> ifFollowing(@Valid CheckIfFollowingRequest request)
    {
        // 1. Validate if the username equals current username and if the username exists.
        String ifFollowingUsername = request.getUsername();
        String currentUsername = UserContextService.getCurrentUser().getUsername();
        if (ifFollowingUsername.equals(currentUsername))
            return ServiceResponse.buildErrorResponse(-1, "You cannot check if you are following yourself.");

        AccountBaseInfo followingAccount = accountBaseInfoMapper.getAccountByUsername(ifFollowingUsername);
        if (followingAccount == null)
            return ServiceResponse.buildErrorResponse(-1, "The username does not exist.");

        // 2. Check if the current user and the following user relation is in user_following table.
        if (userFollowingMapper.countByUsernameAndFollowedUsername(ifFollowingUsername,currentUsername) == 0)
            return ServiceResponse.buildSuccessResponse(false);

        return ServiceResponse.buildSuccessResponse(true);
    }
}
