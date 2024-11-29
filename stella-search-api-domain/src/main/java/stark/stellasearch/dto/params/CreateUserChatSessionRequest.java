package stark.stellasearch.dto.params;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserChatSessionRequest
{
    @NotBlank(message = "Recipient name is required.")
    private String recipientName;
}
