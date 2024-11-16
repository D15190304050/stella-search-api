package stark.stellasearch.dto.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetVideoPlayInfoInPlaylistQueryParam extends PaginationQueryParam
{
    private long userId;
    private long playlistId;
}
