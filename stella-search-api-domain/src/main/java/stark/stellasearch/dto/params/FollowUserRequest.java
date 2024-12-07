package stark.stellasearch.dto.params;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class FollowUserRequest
{
    @Min(value = 1, message = "Minimum user ID is 1.")
    private long userId;
}
