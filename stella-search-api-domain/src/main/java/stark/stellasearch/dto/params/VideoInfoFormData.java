package stark.stellasearch.dto.params;

import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Data
public class VideoInfoFormData
{
    @NotBlank(message = "Title of the video cannot be null.")
    private String title;

    @NotNull(message = "URL of the cover of the video cannot be null.")
    private String coverUrl;

    @Min(value = 1, message = "Minimum video creation type ID is 1.")
    private long videoCreationType;

    @Min(value = 1, message = "Minimum video section ID is 1.")
    private long section;

    @NotNull
    @Size(max = 5, message = "You can select at most 5 labels for 1 video.")
    private List<Long> labels;

    @Size(max = 1000, message = "You can type at most 1000 characters for introduction.")
    private String introduction;

    @Min(value = 1, message = "Minimum video ID is 1.")
    private long videoId;
}
