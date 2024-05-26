package stark.stellasearch.config;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import stark.stellasearch.service.constants.SecurityConstants;

import javax.servlet.http.HttpServletRequest;

public class JsonPersistentTokenBasedRememberServices extends PersistentTokenBasedRememberMeServices
{
    private boolean rememberMeAlways;

    public JsonPersistentTokenBasedRememberServices(String key, UserDetailsService userDetailsService, PersistentTokenRepository tokenRepository)
    {
        super(key, userDetailsService, tokenRepository);
    }

    public void setRememberMeAlways(boolean rememberMeAlways)
    {
        this.rememberMeAlways = rememberMeAlways;
    }

    // We only need to override the rememberMeRequested() method.
    @Override
    protected boolean rememberMeRequested(HttpServletRequest request, String parameter)
    {
        if (rememberMeAlways)
            return true;

        Object rememberMeObject = request.getAttribute(SecurityConstants.REMEMBER_ME);
        if (rememberMeObject != null)
        {
            String rememberMe = rememberMeObject.toString();
            if (rememberMe.equalsIgnoreCase("true") ||
                rememberMe.equalsIgnoreCase("on") ||
                rememberMe.equalsIgnoreCase("yes") ||
                rememberMe.equals("1"))
                return true;
        }

        return super.rememberMeRequested(request, parameter);
    }
}
