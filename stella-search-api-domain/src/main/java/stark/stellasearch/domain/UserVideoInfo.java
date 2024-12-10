package stark.stellasearch.domain;

import lombok.Data;

import lombok.EqualsAndHashCode;
import stark.stellasearch.enums.VideoCreationType;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserVideoInfo extends DomainBase
{
    /**
     * Name of the corresponding object in OSS.
     */
    private String nameInOss;

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
     * Path of the summary file of the video.
     */
    private String summaryFilePath;
}
