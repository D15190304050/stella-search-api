package stark.stellasearch.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserFollowing
{
    private long id;
    private long userId;
    private long followedUserId;
    private int followingStatus;
    private long creatorId;
    private Date creationTime;
    private long modifierId;
    private Date modificationTime;
}
