package stark.stellasearch.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserBlacklist extends DomainBase
{
    private long userId;
    private long blackedUserId;
}
