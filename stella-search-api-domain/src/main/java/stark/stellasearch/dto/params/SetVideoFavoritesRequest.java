package stark.stellasearch.dto.params;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SetVideoFavoritesRequest
{
    @Min(value = 1, message = "Minimum video ID is 1.")
    private long videoId;

    @NotNull(message = "Playlist IDs cannot be null.")
    private List<Long> playlistIds;
}
