package stark.stellasearch.dto.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetUserBlacklistQueryParam extends PaginationQueryParam
{
    private long currentUserId;
}
