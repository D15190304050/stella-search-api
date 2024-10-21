package stark.stellasearch.dto.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddCommentsRequest
{
    @Min(value = 1, message = "Minimum video ID is 1.")
    private long videoId;

    @NotBlank(message = "Content cannot be empty.")
    private String content;

    @Min(value = -2, message = "Parent ID must equal or over -1.")
    private long parentId;
}
