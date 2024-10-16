package stark.stellasearch.dto.params;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
public class UserFollowingRequest
{
    @NotBlank(message = "Username cannot be empty.")
    private String username;
}
