package stark.stellasearch.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import stark.stellasearch.service.JwtService;
import stark.stellasearch.service.constants.SecurityConstants;
import stark.stellasearch.service.dto.AccountPrincipal;
import stark.stellasearch.service.redis.RedisKeyManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class TokenLogoutHandler implements LogoutHandler
{
    @Autowired
    private JwtService jwtService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RedisKeyManager redisKeyManager;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
    {
        String token = request.getHeader(SecurityConstants.TOKEN_HEADER);

        // Remove cached roles and permissions.
        if (token != null)
        {
            AccountPrincipal accountPrincipal = jwtService.parseAccountPrincipal(token);
            if (accountPrincipal != null)
            {
                long accountId = accountPrincipal.getAccountId();
                redisTemplate.delete(redisKeyManager.getUserIdKey(accountId));
            }
        }
    }
}
