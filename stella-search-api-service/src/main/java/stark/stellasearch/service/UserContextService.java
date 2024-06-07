package stark.stellasearch.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import stark.stellasearch.service.dto.User;

public class UserContextService
{
    private UserContextService()
    {

    }

    public static void setAuthentication(UsernamePasswordAuthenticationToken authenticationToken)
    {
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    public static Authentication getAuthentication()
    {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static User getCurrentUser()
    {
        return (User) getAuthentication().getPrincipal();
    }
}
