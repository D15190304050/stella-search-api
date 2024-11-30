package stark.stellasearch.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import stark.stellasearch.domain.UserBlacklist;
import stark.stellasearch.dto.params.BlockUserRequest;
import stark.stellasearch.dto.params.GetBlacklistRequest;
import stark.stellasearch.dto.params.UnblockUserRequest;
import stark.stellasearch.dto.results.UserBlacklistInfo;
import stark.stellasearch.service.UserBlacklistService;
import stark.dataworks.boot.web.ServiceResponse;

import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/user-blacklist")
public class UserBlacklistController
{
    // 1. Block user by username.
    // 2. Unblock user by username.
    // 3. Get user blacklist list.
    // 4. Check whether the user is blocked.

    @Autowired
    private UserBlacklistService userBlacklistService;

    @PostMapping("/block")
    public ServiceResponse<Long> blockUser(@RequestBody BlockUserRequest request)
    {
        return userBlacklistService.blockUserByUsername(request);
    }

    @PostMapping("/unblock")
    public ServiceResponse<Boolean> unblockUser(@RequestBody UnblockUserRequest request)
    {
        return userBlacklistService.unblockUserByUsername(request);
    }

    @GetMapping("/list")
    public ServiceResponse<List<UserBlacklistInfo>> getBlacklistOfCurrentUser(@ModelAttribute GetBlacklistRequest request)
    {
        return userBlacklistService.getBlacklistOfCurrentUser(request);
    }

    @GetMapping("/if-blocked")
    public ServiceResponse<Boolean> ifUserBlocked(@ModelAttribute BlockUserRequest request)
    {
        return userBlacklistService.ifUserBlocked(request);
    }

}
