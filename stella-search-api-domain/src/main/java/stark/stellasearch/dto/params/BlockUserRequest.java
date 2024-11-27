package stark.stellasearch.dto.params;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BlockUserRequest
{
    @NotBlank(message = "Username is required.")
    private String username;
}
