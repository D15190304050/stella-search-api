package stark.stellasearch.dto.results;

import lombok.Data;

import java.util.Date;

@Data
public class UserFollowingInfo
{
    private long userId;
    private String username;
    private String nickname;
    private String avatarUrl;
    private Date followingTime;
    private long followingCount;
    private long followerCount;
    private boolean followState;
}
