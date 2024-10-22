package stark.stellasearch.dto.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetUserFollowingListQueryParam extends PaginationQueryParam
{
    private String username;
}
