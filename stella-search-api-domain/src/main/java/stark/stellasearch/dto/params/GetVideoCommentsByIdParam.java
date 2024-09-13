package stark.stellasearch.dto.params;

import lombok.Data;

@Data
public class GetVideoCommentsByIdParam
{
    private long videoId;
    private long pageCapacity;
    private long offset;
}
