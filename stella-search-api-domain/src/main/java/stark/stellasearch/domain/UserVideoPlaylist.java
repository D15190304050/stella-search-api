package stark.stellasearch.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Video playlists of users.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserVideoPlaylist extends DomainBase
{
    /**
     * ID of the user who owns the playlist.
     */
    private long userId;

    /**
     * Name of the playlist.
     */
    private String name;
}
