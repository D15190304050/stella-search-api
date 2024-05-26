package stark.stellasearch.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import stark.dataworks.basic.data.json.JsonSerializer;
import stark.dataworks.boot.web.CommonErrorResponses;
import stark.dataworks.boot.web.ServiceResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UnauthorizedEntryPoint implements AuthenticationEntryPoint
{
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException
    {
        ServiceResponse<Object> serviceResponse = ServiceResponse.buildErrorResponse(CommonErrorResponses.NOT_LOGIN);
        response.setStatus(HttpStatus.OK.value());
//        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().println(JsonSerializer.serialize(serviceResponse));
        response.flushBuffer();
    }
}
