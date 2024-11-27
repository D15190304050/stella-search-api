package stark.stellasearch.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import stark.stellasearch.dto.params.BlockUserRequest;
import stark.stellasearch.service.UserBlacklistService;
import stark.dataworks.boot.web.ServiceResponse;


@Controller
@ResponseBody
@RequestMapping("/user-blacklist")
public class UserBlacklistController
{
    // TODO:
    // 1. Block user by username.
    // 2. Unblock user by username.
    // 3. Get user blacklist list.
    // 4. Check whether the user is blocked.

    @Autowired
    private UserBlacklistService userBlacklistService;

    @PostMapping("/block")
    public ServiceResponse<Boolean> blockUser(@RequestBody BlockUserRequest request)
    {
        return userBlacklistService.blockUserByUsername(request);
    }


}
