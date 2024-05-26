package stark.stellasearch.security.filter;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;
import stark.dataworks.basic.data.json.JsonSerializer;
import stark.dataworks.boot.ExceptionLogger;
import stark.stellasearch.service.constants.SecurityConstants;
import stark.stellasearch.service.dto.LoginInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UsernamePasswordLoginFilter extends UsernamePasswordAuthenticationFilter
{
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException
    {
        // 1. Determine if it is a POST method.
        // 2. Determine if its content type is "application/json".

        // 1.
        String requestMethod = request.getMethod();
        if (!requestMethod.equalsIgnoreCase(HttpMethod.POST.name()))
            throw new AuthenticationServiceException("Authentication method not supported: " + requestMethod);

        // 2.
        String contentType = request.getContentType();
        if (contentType.equalsIgnoreCase(MediaType.APPLICATION_JSON_VALUE))
        {
            try
            {
                LoginInfo loginInfo = JsonSerializer.deserialize(request.getInputStream(), LoginInfo.class);
                String username = loginInfo.getUsername();
                String password = loginInfo.getPassword();
                String rememberMe = loginInfo.getRememberMe();
                request.setAttribute(SecurityConstants.REMEMBER_ME, rememberMe);
                UsernamePasswordAuthenticationToken authenticationRequest = new UsernamePasswordAuthenticationToken(username, password);
                setDetails(request, authenticationRequest);

                String redirectUrl = loginInfo.getRedirectUrl();
                if (StringUtils.hasText(redirectUrl))
                    request.setAttribute(SecurityConstants.REDIRECT_URL, redirectUrl);

                return getAuthenticationManager().authenticate(authenticationRequest);
            }
            catch (IOException e)
            {
                ExceptionLogger.logExceptionInfo(e);
            }
        }

        // Call attemptAuthentication() of super class.
        return super.attemptAuthentication(request, response);
    }
}
