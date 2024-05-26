package stark.stellasearch.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginInfo
{
    private String username;
    private String password;
    private String rememberMe;
    private String redirectUrl;
}
