package stark.stellasearch.dto.results;

import lombok.Data;

import java.util.Date;

@Data
public class UserBlacklistInfo
{
    private long id;
    private long userId;
    private long blockedUserId;
    private String blockedUsername;
    private Date blockedTime;
}
