package stark.stellasearch.dto.params;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class DeleteCommentsRequest
{
    @Min(value = 1, message = "Minimum video ID is 1.")
    private long id;
}
