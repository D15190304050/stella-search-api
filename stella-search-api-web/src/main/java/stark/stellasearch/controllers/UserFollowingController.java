package stark.stellasearch.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import stark.dataworks.boot.web.PaginatedData;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.dto.params.*;
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
    public ServiceResponse<Boolean> followUser(@RequestBody FollowUserRequest request)
    {
        return userFollowingService.followUser(request);
    }

    @PostMapping("/unfollow")
    public ServiceResponse<Boolean> unfollowUser(@RequestBody UnfollowUserRequest request)
    {
        return userFollowingService.unfollowUser(request);
    }

    @GetMapping("/followings")
    public ServiceResponse<PaginatedData<UserFollowingInfo>> getFollowings(@ModelAttribute GetFollowingsRequest request)
    {
        return userFollowingService.getFollowings(request);
    }

    @GetMapping("/followers")
    public ServiceResponse<PaginatedData<UserFollowingInfo>> getFollowers(@ModelAttribute GetFollowersRequest request)
    {
        return userFollowingService.getFollowers(request);
    }

    @GetMapping("/if-following")
    public ServiceResponse<Boolean> ifFollowing(@ModelAttribute CheckIfFollowingRequest request)
    {
        return userFollowingService.ifFollowing(request);
    }
}
