package stark.stellasearch.dto.params;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CreatePlaylistRequest
{
    @NotBlank(message = "The playlist name is required")
    private String name;

    @Size(max = 1000, message = "You can type at most 1000 characters for description.")
    private String description;
}