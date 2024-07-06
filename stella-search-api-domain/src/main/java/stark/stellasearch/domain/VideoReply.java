package stark.stellasearch.domain;

import lombok.Data;

import java.util.Date;

@Data
public class VideoReply
{
    /**
     * ID of the video.
     */
    private long id;

    /**
     * ID of the video that the reply is associated with.
     */
    private long videoId;

    /**
     * Content of the reply.
     */
    private String content;

    /**
     * ID of the parent reply. -1 if no parent.
     */
    private long parentId;

    /**
     * ID of the user who replies.
     */
    private long creatorId;

    /**
     * Creation time of the reply.
     */
    private Date creationTime;

    /**
     * ID of the user who modifies the reply. Since all replies are read only, modifierId will always be same as creatorId.
     */
    private long modifierId;

    /**
     * Modification time of the reply. Since all replies are read only, modificationTime will always be same as creationTime.
     */
    private Date modificationTime;
}
