package stark.stellasearch.dto.results;

import lombok.Data;

import java.util.List;

@Data
public class UserFollowingInfo
{
    private long userId;
    private String username;
    private String nickname;
    private String avatarUrl;
    private long followingCount;
    private long followerCount;
}
