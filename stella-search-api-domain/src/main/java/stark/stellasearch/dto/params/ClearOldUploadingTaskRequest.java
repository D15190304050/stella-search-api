package stark.stellasearch.dto.params;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ClearOldUploadingTaskRequest
{
    @NotBlank(message = "Video uploading task ID must not be blank.")
    private String videoUploadingTaskId;
}
