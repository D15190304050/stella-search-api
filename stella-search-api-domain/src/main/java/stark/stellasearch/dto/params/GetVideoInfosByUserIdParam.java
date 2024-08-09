package stark.stellasearch.dto.params;

import lombok.Data;

@Data
public class GetVideoInfosByUserIdParam
{
    private long userId;
    private long pageCapacity;
    private long offset;
}
