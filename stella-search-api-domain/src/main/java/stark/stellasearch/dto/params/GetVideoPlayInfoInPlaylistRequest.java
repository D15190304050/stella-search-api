package stark.stellasearch.dto.params;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetVideoPlayInfoInPlaylistRequest extends PaginationRequestParam
{
    @Min(value = 1, message = "Minimum playlist ID is 1.")
    private long playlistId;
}
