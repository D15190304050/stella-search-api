package stark.stellasearch.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Comments of videos.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserVideoComment extends DomainBase
{
    /**
     * ID of the user who comments.
     */
    private long userId;

    /**
     * ID of the video that the comment is associated with.
     */
    private long videoId;

    /**
     * Content of the comment.
     */
    private String content;

    /**
     * ID of the parent comment. -1 if no parent.
     */
    private long parentId;
}
