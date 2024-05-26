package stark.stellasearch.dto.results;

import lombok.Data;

@Data
public class LoginState
{
    private long id;
    private String username;
    private String nickname;
    private String avatarUrl;
    private String token;
}
