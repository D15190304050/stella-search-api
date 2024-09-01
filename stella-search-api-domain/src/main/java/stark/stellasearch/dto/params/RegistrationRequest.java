package stark.stellasearch.dto.params;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RegistrationRequest
{
    @NotBlank(message = "Please input your username!")
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9\\-_#@!$%]{4,32}$", message = "The username can only starts with an English alphabet and can only contains English alphabets, numbers, '_'. And a username should contains 5 - 32 characters.")
    private String username;

    @NotBlank(message = "Please input your password!")
    @Pattern(regexp = "[A-Za-z0-9\\-_#@!$%]{8,32}")
    private String password;

    @NotBlank(message = "Please confirm your password!")
    private String confirmedPassword;

    @NotBlank(message = "Please input your nickname!")
    @Pattern(regexp = "^[A-Za-z][A-Za-z0-9\\-_#@!$%]{4,32}$", message = "The nickname can only starts with an English alphabet and can only contains English alphabets, numbers, '_'. And a username should contains 5 - 32 characters.")
    private String nickname;

    @AssertTrue(message = "You must accept the agreement to register your account.")
    private boolean acceptAgreement;

    @NotBlank(message = "Please upload your avatar.")
    private String avatarUrl;

    @NotBlank(message = "Please input the captcha.")
    @Length(min = 4, max = 4)
    private String captcha;

    @NotBlank(message = "Please provide captcha ID.")
    @Pattern(regexp = "^Captcha-[0-9a-f]{8}(-[0-9a-f]{4}){3}-[0-9a-f]{12}$", message = "No captcha Id.")
    private String captchaId;

    @NotBlank(message = "Please input your phone number!")
    @Pattern(regexp = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$", message = "Please enter a correct phone number.")
    private String phoneNumber;

    @NotBlank(message = "Phone number prefix cannot be empty.")
    private String phoneNumberPrefix;

    @NotBlank(message = "Gender cannot be empty.")
    private String gender;

    @NotBlank
    @Email(message = "The text of email is not valid E-mail!")
    private String email;
}
