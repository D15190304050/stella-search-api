package stark.stellasearch.dto.params;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class DeletePlaylistRequest
{
    @Min(value = 1, message = "Minimum playlist ID is 1.")
    private long id;
}
