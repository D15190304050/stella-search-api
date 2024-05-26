package stark.stellasearch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import stark.stellasearch.dao.AccountBaseInfoMapper;
import stark.stellasearch.domain.AccountBaseInfo;
import stark.stellasearch.service.dto.User;

@Slf4j
@Component
public class DaoUserDetailService implements UserDetailsService, UserDetailsPasswordService
{
    @Autowired
    private AccountBaseInfoMapper accountBaseInfoMapper;

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword)
    {
        log.info("Enter updatePassword() ...");

        int updateCount = accountBaseInfoMapper.updatePasswordByUsername(user.getUsername(), newPassword);
        if (updateCount == 1)
            ((User)user).setPassword(newPassword);

        log.info("Execute updatePassword() success ...");

        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        AccountBaseInfo accountBaseInfo = accountBaseInfoMapper.getAccountByUsername(username);
        User user = new User();
        user.setUsername(accountBaseInfo.getUsername());
        user.setPassword(accountBaseInfo.getEncryptedPassword());
        user.setId(accountBaseInfo.getId());
        user.setNickname(accountBaseInfo.getNickname());
        user.setAvatarUrl(accountBaseInfo.getAvatarUrl());
        user.setEmail(accountBaseInfo.getEmail());
        user.setGender(accountBaseInfo.getGender());

        return user;
    }
}
