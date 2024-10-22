package stark.stellasearch.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import stark.dataworks.boot.web.PaginatedData;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.dto.params.CheckIfFollowingRequest;
import stark.stellasearch.dto.params.GetUserFollowingListRequest;
import stark.stellasearch.dto.params.UserFollowingRequest;
import stark.stellasearch.dto.params.UserUnfollowingRequest;
import stark.stellasearch.dto.results.UserFollowingInfo;
import stark.stellasearch.service.UserFollowingService;

@Slf4j
@Controller
@ResponseBody
@RequestMapping("/user-following")
public class UserFollowingController
{
    // TODO:
    // 1. Follow another user.
    // 2. Unfollow another user.
    // 3. Get the user's following list.
    // 4. Get the user's follower list.
    // 5. Check if the current user is following another user.
    @Autowired
    private UserFollowingService userFollowingService;

    @PostMapping("/follow")
    public ServiceResponse<Boolean> userFollowing(@RequestBody UserFollowingRequest request)
    {
        return userFollowingService.follow(request);
    }

    @PostMapping("/unfollow")
    public ServiceResponse<Boolean> userUnfollowing(@RequestBody UserUnfollowingRequest request)
    {
        return userFollowingService.unfollow(request);
    }

    @GetMapping("/following-list")
    public ServiceResponse<PaginatedData<UserFollowingInfo>> getFollowingList(@ModelAttribute GetUserFollowingListRequest request)
    {
        return userFollowingService.getFollowingList(request);
    }

    @GetMapping("/follower-list")
    public ServiceResponse<PaginatedData<UserFollowingInfo>> getFollowerList(@ModelAttribute GetUserFollowingListRequest request)
    {
        return userFollowingService.getFollowerList(request);
    }

    @GetMapping("/if-following")
    public ServiceResponse<Boolean> ifFollowing(@ModelAttribute CheckIfFollowingRequest request)
    {
        return userFollowingService.ifFollowing(request);
    }
}
