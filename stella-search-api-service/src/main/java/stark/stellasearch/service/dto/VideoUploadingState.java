package stark.stellasearch.service.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class VideoUploadingState
{
    private String videoName;
    private long videoChunkCount;
    private String videoUploadingTaskId;
    private List<Long> finishedChunkIndexes;

}
