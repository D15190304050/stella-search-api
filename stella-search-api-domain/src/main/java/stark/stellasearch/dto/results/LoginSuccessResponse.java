package stark.stellasearch.dto.results;

import lombok.Data;

@Data
public class LoginSuccessResponse
{
    // Should be account base information, like nickname, avatar_url, ...
    private String nickname;
    private String avatar_url;
    private String token;
}
