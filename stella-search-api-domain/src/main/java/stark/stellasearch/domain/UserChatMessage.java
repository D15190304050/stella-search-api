package stark.stellasearch.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserChatMessage extends DomainBase
{
    private long sessionId;
    private long senderId;
    private long recipientId;
    private String content;
}
