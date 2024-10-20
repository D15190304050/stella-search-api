package stark.stellasearch.dto.params;

import lombok.Data;

import jakarta.validation.constraints.Min;

@Data
public class CancelLikeVideoRequest
{
    @Min(value = 1, message = "Minimum video ID is 1.")
    private long videoId;
}
