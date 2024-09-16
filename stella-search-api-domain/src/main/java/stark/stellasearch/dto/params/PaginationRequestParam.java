package stark.stellasearch.dto.params;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Data
public class PaginationRequestParam
{
    @Min(value = 1, message = "Page capacity must be >= 1.")
    @Max(value = 100, message = "Page capacity must be <= 100.")
    private long pageCapacity;

    @Min(value = 1, message = "Page index must be a positive integer.")
    private long pageIndex;
}
