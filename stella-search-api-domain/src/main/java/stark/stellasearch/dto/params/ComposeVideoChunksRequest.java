package stark.stellasearch.dto.params;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class ComposeVideoChunksRequest
{
    @NotBlank(message = "Video uploading task ID must not be blank.")
    private String videoUploadingTaskId;
}
