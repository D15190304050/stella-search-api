package stark.stellasearch.dto.params;

import lombok.Data;

@Data
public class GetUserFavoritesByPlaylistIdParam
{
    private long playlistId;
    private long userId;
    private long pageCapacity;
    private long offset;
}
