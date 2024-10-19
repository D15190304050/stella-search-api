package stark.stellasearch.dto.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetUserFavoritesByPlaylistIdParam extends PaginationQueryParam
{
    private long playlistId;
    private long userId;
}
