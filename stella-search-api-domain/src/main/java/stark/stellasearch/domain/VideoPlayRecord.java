package stark.stellasearch.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Play records of videos.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class VideoPlayRecord extends DomainBase
{
    /**
     * ID of the user who watched the video.
     */
    private long userId;

    /**
     * ID of the video that is watched.
     */
    private long videoId;
}
