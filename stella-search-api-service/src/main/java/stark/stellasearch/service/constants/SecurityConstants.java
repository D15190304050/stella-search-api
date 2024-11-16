package stark.stellasearch.service.constants;

import org.springframework.http.HttpHeaders;

public class SecurityConstants
{
    private SecurityConstants(){}

    public static final boolean ALWAYS_REMEMBER_ME = true;
    public static final String USER_ID = "user_id";
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String USERNAME = "username";
    public static final String NICKNAME = "nickname";
    public static final String PASSWORD = "password";
    public static final String REMEMBER_ME = "rememberMe";
    public static final String TOKEN_HEADER = HttpHeaders.AUTHORIZATION;
    public static final String LOGIN_SUCCESS = "Login success.";
    public static final String LOGOUT_SUCCESS = "Logout success.";
    public static final String DEFAULT_LOGIN_URI = "/login";
    public static final String DEFAULT_LOGOUT_URI = "/logout";

    public static final String SSO_COOKIE_NAME = "stella_login";
    public static final String REDIRECT_URL = "redirect_url";

    public static final String[] NON_AUTHENTICATE_URIS =
            {
                    "/connection/hello",
                    "/account/captcha",
                    "/avatar/*",
                    "/account/register",
                    "/account/validate-token",
                    "/video/cover/*",
                    "/video/play",
                    "/comment/list"
            };
}
