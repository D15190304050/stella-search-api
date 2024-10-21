package stark.stellasearch.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.dto.params.RegistrationRequest;
import stark.stellasearch.service.AccountService;
import stark.stellasearch.service.CaptchaService;
import stark.stellasearch.dto.results.CaptchaResponse;
import stark.stellasearch.dto.results.LoginState;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/account")
public class AccountController
{
    @Autowired
    private CaptchaService captchaService;

    @Autowired
    private AccountService accountService;

    @GetMapping("/login")
    public ServiceResponse<LoginState> login()
    {
        LoginState loginState = new LoginState();

        loginState.setId(1);
        loginState.setNickname("Chris");
        loginState.setUsername("Chris");

        return ServiceResponse.buildSuccessResponse(loginState);
    }

    @GetMapping("/captcha")
    public ServiceResponse<CaptchaResponse> generateCaptcha(HttpServletResponse response) throws IOException
    {
        return captchaService.generateCaptcha(response);
    }

    @GetMapping("/login-state")
    public ServiceResponse<LoginState> getLoginStateByCookie()
    {
        return accountService.getLoginStateByCookie();
    }

    @PostMapping("/register")
    public ServiceResponse<Boolean> register(@RequestBody RegistrationRequest registrationRequest)
    {
        return accountService.register(registrationRequest);
    }
}
