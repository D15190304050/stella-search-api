package stark.stellasearch.dto.params;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendUserChatMessageRequest
{
    @Min(value = 1, message = "Recipient id must be over 0.")
    private long recipientId;

    @NotBlank(message = "Content is required.")
    private String content;
}
