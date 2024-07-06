package stark.stellasearch.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import stark.dataworks.basic.data.json.JsonSerializer;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.dto.results.LoginStateToken;
import stark.stellasearch.service.JwtService;
import stark.stellasearch.service.constants.SecurityConstants;
import stark.stellasearch.service.dto.User;
import stark.stellasearch.service.redis.StellaRedisOperation;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class LoginSuccessJsonHandler implements AuthenticationSuccessHandler
{
    @Autowired
    private JwtService jwtService;

    @Autowired
    private StellaRedisOperation redisOperation;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException
    {
        User user = (User) authentication.getPrincipal();
        cacheAuthentication(user);
        writeAuthenticationToken(request, response, user);
    }

    public String prepareUserLoginInfoToken(User user)
    {
        return "";
    }

    // For SSO, we only need to return a token.
    // Then other system can get user info like username by token.
    // Without SSO, we can return all the information.
    public void writeAuthenticationToken(HttpServletRequest request, HttpServletResponse response, User user) throws IOException
    {
        Object redirectUrlAttribute = request.getAttribute(SecurityConstants.REDIRECT_URL);
        String redirectUrl = redirectUrlAttribute == null ? null : (String) redirectUrlAttribute;
        log.info("redirectUrl = {}", redirectUrl);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        LoginStateToken loginStateToken = generateLoginStateToken(user);

        ServiceResponse<LoginStateToken> loginSuccessResponse = ServiceResponse.buildSuccessResponse(loginStateToken, SecurityConstants.LOGIN_SUCCESS);
        String resultJson = JsonSerializer.serialize(loginSuccessResponse);
        log.info("Login success message = {}", resultJson);
        response.getWriter().println(resultJson);

        response.flushBuffer();
    }

    // TODO: Move this method to another class after integration of other login methods.
    private void cacheAuthentication(User user)
    {
        // Cache user info.
        redisOperation.cacheUser(user);
    }

    private LoginStateToken generateLoginStateToken(User user)
    {
        String token = jwtService.createToken(user);

        LoginStateToken loginStateToken = new LoginStateToken();

//        loginStateToken.setId(user.getId());
//        loginStateToken.setUsername(user.getUsername());
//        loginStateToken.setNickname(user.getNickname());
//        loginStateToken.setAvatarUrl(user.getAvatarUrl());
        loginStateToken.setAccessToken(token);

        return loginStateToken;
    }
}
