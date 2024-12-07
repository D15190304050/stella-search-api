package stark.stellasearch.service;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import stark.dataworks.boot.web.PaginatedData;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.dao.AccountBaseInfoMapper;
import stark.stellasearch.dao.UserFollowingMapper;
import stark.stellasearch.domain.AccountBaseInfo;
import stark.stellasearch.domain.UserFollowing;
import stark.stellasearch.dto.params.*;
import stark.stellasearch.dto.results.UserFollowingInfo;
import stark.stellasearch.service.dto.User;

import java.util.List;

@Slf4j
@Service
@Validated
public class UserFollowingService
{
    private static final int FOLLOW_STATUS_FOLLOWING = 1;

    @Autowired
    private AccountBaseInfoMapper accountBaseInfoMapper;

    @Autowired
    private UserFollowingMapper userFollowingMapper;

    public ServiceResponse<Boolean> followUser(@Valid FollowUserRequest request)
    {
        // 1. Validate if the username is not the same as the current user.
        User currentUser = UserContextService.getCurrentUser();
        long currentUserId = currentUser.getId();
        long followingUserId = request.getUserId();
        if (currentUser.getId() == followingUserId)
            return ServiceResponse.buildErrorResponse(-1, "You cannot follow yourself.");

        // 2. Validate if the username exists.
        long followingUserCount = accountBaseInfoMapper.countByUserId(followingUserId);
        if (followingUserCount == 0)
            return ServiceResponse.buildErrorResponse(-1, "The user ID does not exist.");

        // 3. Validate if the user is already following the user.
        long userFollowingRecordCount = userFollowingMapper.countUserFollowingsByUserIds(currentUserId, followingUserId);
        if (userFollowingRecordCount > 0)
            return ServiceResponse.buildErrorResponse(-1, "You have already followed the user.");

        // 4. Insert into the user_following table.
        UserFollowing userFollowingInfo = new UserFollowing();
        userFollowingInfo.setUserId(currentUserId);
        userFollowingInfo.setFollowedUserId(followingUserId);
        userFollowingInfo.setFollowingStatus(FOLLOW_STATUS_FOLLOWING);
        userFollowingInfo.setCreatorId(currentUserId);
        userFollowingInfo.setModifierId(currentUserId);
        userFollowingMapper.insert(userFollowingInfo);

        return ServiceResponse.buildSuccessResponse(true);
    }

    public ServiceResponse<Boolean> unfollowUser(@Valid UnfollowUserRequest request)
    {
        // 1. Validate if the username is not the same as the current user.
        User currentUser = UserContextService.getCurrentUser();
        long currentUserId = currentUser.getId();
        long followingUserId = request.getUserId();
        if (currentUserId == followingUserId)
            return ServiceResponse.buildErrorResponse(-1, "You cannot unfollow yourself.");

        // 2. Validate if the username exists.
        long followingUserCount = accountBaseInfoMapper.countByUserId(followingUserId);
        if (followingUserCount == 0)
            return ServiceResponse.buildErrorResponse(-1, "The username ID not exist.");

        // 3. Delete from the user_following table.
        userFollowingMapper.deleteByUserIdAndFollowedUserId(currentUserId, followingUserId);
        return ServiceResponse.buildSuccessResponse(true);
    }

    public ServiceResponse<PaginatedData<UserFollowingInfo>> getFollowings(@Valid GetFollowingsRequest request)
    {
        long userId = UserContextService.getCurrentUser().getId();

        // 1. Get the following user or follower list.
        GetUserFollowingListQueryParam userFollowingListQueryParam = new GetUserFollowingListQueryParam();
        userFollowingListQueryParam.setUserId(userId);
        userFollowingListQueryParam.setPaginationParam(request);

        List<UserFollowingInfo> userFollowingInfoList = userFollowingMapper.getFollowings(userFollowingListQueryParam);
        userFollowingInfoList.forEach(x -> x.setFollowState(true));
        long total = userFollowingMapper.countFollowingsByUserId(userId);
        return assembleUserFollowingInfoResponse(userFollowingInfoList, total);
    }

    public ServiceResponse<PaginatedData<UserFollowingInfo>> getFollowers(@Valid GetFollowersRequest request)
    {
        long userId = UserContextService.getCurrentUser().getId();

        // 1. Get the following user or follower list.
        GetUserFollowingListQueryParam userFollowingListQueryParam = new GetUserFollowingListQueryParam();
        userFollowingListQueryParam.setUserId(userId);
        userFollowingListQueryParam.setPaginationParam(request);

        List<UserFollowingInfo> userFollowingInfoList = userFollowingMapper.getFollowers(userFollowingListQueryParam);
        long total = userFollowingMapper.countFollowersByUserId(userId);
        return assembleUserFollowingInfoResponse(userFollowingInfoList, total);
    }

    @NotNull
    private ServiceResponse<PaginatedData<UserFollowingInfo>> assembleUserFollowingInfoResponse(List<UserFollowingInfo> userFollowingInfoList, long total)
    {
        PaginatedData<UserFollowingInfo> response = new PaginatedData<>();
        response.setData(userFollowingInfoList);
        response.setTotal(total);
        ServiceResponse<PaginatedData<UserFollowingInfo>> paginatedDataResponse = ServiceResponse.buildSuccessResponse(response);
        paginatedDataResponse.putExtra("size", userFollowingInfoList.size());
        return paginatedDataResponse;
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
