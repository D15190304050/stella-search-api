package stark.stellasearch.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccountPrincipal
{
    private long accountId;
    private String username;
    private String nickname;

    public AccountPrincipal(User user)
    {
        this(user.getId(), user.getUsername(), user.getNickname());
    }
}
