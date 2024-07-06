package stark.stellasearch.domain;

import lombok.Data;

import java.util.Date;

@Data
public class VideoUploadingTask
{
    /**
     * Auto increment ID.
     */
    private long id;

    /**
     * Task ID.
     */
    private String taskId;

    /**
     * State of the video uploading task: 0 - Created; 1 - Completed; 2 - Aborted.
     */
    private int state;

    /**
     * ID of the creator of the video uploading task, i.e., ID of who uploads the video.
     */
    private long creatorId;

    /**
     * Creation time of the video uploading task.
     */
    private Date creationTime;

    /**
     * ID of the modifier of the video uploading task, i.e., ID of who uploads the video.
     */
    private long modifierId;

    /**
     * Modification time of the video uploading task.
     */
    private Date modificationTime;
}
