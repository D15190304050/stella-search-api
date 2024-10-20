package stark.stellasearch.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import stark.stellasearch.service.dto.User;

public class UserContextService
{
    private static final User ANONYMOUS_USER;

    static
    {
        ANONYMOUS_USER = new User();
        ANONYMOUS_USER.setUsername("anonymous");
        ANONYMOUS_USER.setPassword("anonymous");
        ANONYMOUS_USER.setId(-1);
    }

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

    /**
     * Returns the currently logged-in user or anonymous user if there is no login state.
     * @return The currently logged-in user or anonymous user if there is no login state.
     */
    public static User getCurrentUser()
    {
        Object principal = getAuthentication().getPrincipal();
        if (principal instanceof User user)
            return user;

        return ANONYMOUS_USER;
    }
}
