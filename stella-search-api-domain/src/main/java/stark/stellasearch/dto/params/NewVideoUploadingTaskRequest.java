package stark.stellasearch.dto.params;

import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Data
public class NewVideoUploadingTaskRequest
{
    @Min(value = 1, message = "Number of video chunks must be >= 1.")
    private long videoChunkCount;

    @NotBlank(message = "File extension of the video must be provided.")
    private String videoFileExtension;
}
