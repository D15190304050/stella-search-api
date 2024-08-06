package stark.stellasearch.domain;

import lombok.Data;

import java.util.Date;
import stark.stellasearch.enums.VideoCreationType;

@Data
public class UserVideoInfo
{
    /**
     * ID of the video.
     */
    private long id;

    /**
     * URL of the video.
     */
    private String videoUrl;

    /**
     * Title of the video.
     */
    private String title;

    /**
     * URL of the cover of the video.
     */
    private String coverUrl;

    /**
     * Creation type of the video: 0 - Original; 1 - Reprint.
     * See {@link VideoCreationType} for more detailed definition.
     */
    private long creationTypeId;

    /**
     * ID of the section that the video belongs to.
     */
    private long sectionId;

    /**
     * Labels of the video, separated by ",", e.g., "Game,Challenge".
     */
    private String labelIds;

    /**
     * Introduction to the video.
     */
    private String introduction;

    /**
     * ID of the user who uploads the video.
     */
    private long creatorId;

    /**
     * Creation time of the video uploading task.
     */
    private Date creationTime;

    /**
     * ID of the modifier of the video information, i.e., ID of the user who uploads the video information.
     */
    private long modifierId;

    /**
     * Modification time of the video information.
     */
    private Date modificationTime;
}
