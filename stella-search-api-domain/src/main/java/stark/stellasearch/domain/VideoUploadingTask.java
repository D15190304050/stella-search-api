package stark.stellasearch.domain;

import lombok.Data;

import lombok.EqualsAndHashCode;
import stark.stellasearch.enums.VideoUploadingTaskState;

@EqualsAndHashCode(callSuper = true)
@Data
public class VideoUploadingTask extends DomainBase
{
    /**
     * Task ID.
     */
    private String taskId;

    /**
     * State of the video uploading task: 0 - Created; 1 - Completed; 2 - Aborted; 3 - To be deleted; 4 - Deleted; 5 - Used.
     * See {@link VideoUploadingTaskState} for more detailed definition.
     */
    private int state;
}
