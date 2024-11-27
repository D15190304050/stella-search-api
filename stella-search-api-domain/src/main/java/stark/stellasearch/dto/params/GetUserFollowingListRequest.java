package stark.stellasearch.dto.params;

import lombok.Data;

@Data
public class GetUserFollowingListRequest extends PaginationRequestParam
{
    private String username;
}
