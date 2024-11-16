package stark.stellasearch.dto.params;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Data
public class VideoChunkUploadingRequest
{
    @NotBlank(message = "Video uploading task ID must not be blank.")
    private String videoUploadingTaskId;

    @Min(value = 0, message = "Video chunk index must be >= 0.")
    private long videoChunkIndex;

    @JsonIgnore
    private MultipartFile videoChunk;
}
