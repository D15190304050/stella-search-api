package stark.stellasearch.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccountBaseInfo
{
    private long id;
    private String phoneNumber;
    private String encryptedPassword;
    private String username;
    private String nickname;
    private String avatarUrl;
    private String email;
    private String phoneNumberPrefix;
    private String gender;
    private int state;
    private Date creationTime;
    private Date updateTime;
}