package stark.stellasearch.dto.params;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CheckIfFollowingRequest
{
    @NotBlank(message = "Username cannot be empty.")
    private String username;
}