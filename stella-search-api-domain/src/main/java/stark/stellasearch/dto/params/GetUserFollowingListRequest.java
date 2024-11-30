package stark.stellasearch.dto.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GetUserFollowingListRequest extends PaginationRequestParam
{
    private String username;
}
