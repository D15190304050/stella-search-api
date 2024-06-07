package stark.stellasearch.dto.params;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class NewVideoUploadingTaskRequest
{
    @NotBlank(message = "Video name must not be blank.")
    private String videoName;

    @Min(value = 1, message = "Number of video chunks must be >= 1.")
    private long videoChunkCount;
}
