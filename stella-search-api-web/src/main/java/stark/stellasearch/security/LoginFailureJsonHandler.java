package stark.stellasearch.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import stark.dataworks.basic.data.json.JsonSerializer;
import stark.dataworks.basic.exceptions.ExceptionInfoFormatter;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.dto.results.LoginStateToken;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class LoginFailureJsonHandler implements AuthenticationFailureHandler
{
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException
    {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        String exceptionInfo = ExceptionInfoFormatter.formatMessageAndStackTrace(exception);
        log.error("Login failure: {}", exceptionInfo);

        ServiceResponse<LoginStateToken> loginResult = ServiceResponse.buildErrorResponse(-1, exceptionInfo);
        String resultJson = JsonSerializer.serialize(loginResult);
        response.getWriter().println(resultJson);
        response.flushBuffer();
    }
}
