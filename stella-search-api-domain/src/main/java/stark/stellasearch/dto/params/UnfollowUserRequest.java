package stark.stellasearch.dto.params;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class UnfollowUserRequest
{
    @Min(value = 1, message = "Minimum user ID is 1.")
    private long userId;
}
