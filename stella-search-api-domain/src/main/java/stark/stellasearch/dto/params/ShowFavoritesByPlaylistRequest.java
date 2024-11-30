package stark.stellasearch.dto.params;

import lombok.Data;

import jakarta.validation.constraints.Min;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ShowFavoritesByPlaylistRequest extends PaginationRequestParam
{
    @Min(value = 1, message = "Minimum playlist ID is 1.")
    private long playlistId;
}
