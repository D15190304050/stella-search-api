package stark.stellasearch.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User video favorites.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserVideoFavorites extends DomainBase
{
    /**
     * User ID, who adds the video to favorites.
     */
    private long userId;

    /**
     * ID of the video that is added to favorites.
     */
    private long videoId;

    /**
     * ID of the playlist that contains the video.
     */
    private long playlistId;
}
