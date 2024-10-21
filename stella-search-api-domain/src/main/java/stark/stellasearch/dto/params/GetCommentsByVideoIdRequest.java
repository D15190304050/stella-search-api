package stark.stellasearch.dto.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.Min;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetCommentsByVideoIdRequest extends PaginationRequestParam
{
    @Min(value = 1, message = "Minimum video ID is 1.")
    private long videoId;
}
