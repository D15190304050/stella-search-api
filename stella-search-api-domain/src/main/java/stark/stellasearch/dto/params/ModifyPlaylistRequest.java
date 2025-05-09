package stark.stellasearch.dto.params;

import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class ModifyPlaylistRequest
{
    @Min(value = 1, message = "Minimum playlist ID is 1.")
    private long id;

    @NotBlank(message = "The playlist name is required")
    private String name;

    @Size(max = 1000, message = "You can type at most 1000 characters for description.")
    private String description;
}
