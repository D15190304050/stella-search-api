package stark.stellasearch.dto.params;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class DeleteUserChatSessionRequest
{
    @Min(value = 0, message = "Session id must be over 0.")
    private long sessionId;
}
