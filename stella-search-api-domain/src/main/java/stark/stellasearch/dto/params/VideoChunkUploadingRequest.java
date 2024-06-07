package stark.stellasearch.dto.params;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class VideoChunkUploadingRequest
{
    @NotBlank(message = "Video name must not be blank.")
    private String videoName;

    @NotBlank(message = "Video uploading task ID must not be blank.")
    private String videoUploadingTaskId;

    @Min(value = 0, message = "Video chunk index must be >= 0.")
    private long videoChunkIndex;


    private long videoChunkSize; // Fixed.
    private MultipartFile videoChunk; // 1 time.
}
