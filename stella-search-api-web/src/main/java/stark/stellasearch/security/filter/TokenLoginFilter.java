package stark.stellasearch.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import stark.dataworks.basic.data.json.JsonSerializer;
import stark.dataworks.basic.data.redis.RedisQuickOperation;
import stark.dataworks.boot.web.TokenHandler;
import stark.stellasearch.service.JwtService;
import stark.stellasearch.service.UserContextService;
import stark.stellasearch.service.constants.SecurityConstants;
import stark.stellasearch.service.dto.AccountPrincipal;
import stark.stellasearch.service.dto.User;
import stark.stellasearch.service.redis.RedisKeyManager;
import stark.stellasearch.service.redis.StellaRedisOperation;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class TokenLoginFilter extends OncePerRequestFilter
{
    private final JwtService jwtService;
    private final RedisQuickOperation redisQuickOperation;
    private final UserDetailsService userDetailsService;
    private final StellaRedisOperation stellaRedisOperation;
    private final RedisKeyManager redisKeyManager;
    private final List<String> ignoreUris;

    public TokenLoginFilter(JwtService jwtService, RedisQuickOperation redisQuickOperation, UserDetailsService userDetailsService, StellaRedisOperation stellaRedisOperation, String contextPath, RedisKeyManager redisKeyManager)
    {
        this.jwtService = jwtService;
        this.redisQuickOperation = redisQuickOperation;
        this.userDetailsService = userDetailsService;
        this.stellaRedisOperation = stellaRedisOperation;
        this.redisKeyManager = redisKeyManager;

        ignoreUris = new ArrayList<>();
        for (String uri : SecurityConstants.NON_AUTHENTICATE_URIS)
            ignoreUris.add(contextPath + uri);
        ignoreUris.add(contextPath + SecurityConstants.DEFAULT_LOGIN_URI);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        // Parse the login state if there is a token, no matter if it is a login state required Uri.

        String token = TokenHandler.getToken(request, SecurityConstants.SSO_COOKIE_NAME);

        if (StringUtils.hasText(token))
        {
            AccountPrincipal accountPrincipal = jwtService.parseAccountPrincipal(token);
            if (accountPrincipal != null)
            {
                long accountId = accountPrincipal.getAccountId();
                String userJson = redisQuickOperation.get(redisKeyManager.getUserIdKey(accountId));
                User user;

                if (StringUtils.hasText(userJson))
                    user = JsonSerializer.deserialize(userJson, User.class);
                else
                {
                    user = (User) userDetailsService.loadUserByUsername(accountPrincipal.getUsername());
                    stellaRedisOperation.cacheUser(user);
                }

                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
                UserContextService.setAuthentication(authenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
