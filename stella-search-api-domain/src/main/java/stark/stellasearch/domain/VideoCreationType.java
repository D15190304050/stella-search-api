package stark.stellasearch.domain;

import lombok.Data;

import java.util.Date;

@Data
public class VideoCreationType
{
    /**
     * ID of the video creation type.
     */
    private long id;

    /**
     * Video creation type: 0 - Original; 1 - Reprinting.
     */
    private String type;

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
