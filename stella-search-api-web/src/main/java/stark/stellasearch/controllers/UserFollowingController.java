package stark.stellasearch.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.dto.params.UserFollowingRequest;
import stark.stellasearch.dto.params.UserUnfollowingRequest;
import stark.stellasearch.service.UserFollowingService;

@Slf4j
@Controller
@ResponseBody
@RequestMapping("/user-following")
public class UserFollowingController
{
    // TODO:
    // 1. 关注用户
    // 2. 取消关注用户
    // 3. 获取用户的关注列表
    // 4. 获取用户的粉丝列表
    // 5. 判断用户是否关注了另一个用户
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
}
