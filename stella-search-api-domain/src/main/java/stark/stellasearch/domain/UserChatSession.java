package stark.stellasearch.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserChatSession extends DomainBase
{
    private long user1Id;
    private long user2Id;
    private int status;
}
