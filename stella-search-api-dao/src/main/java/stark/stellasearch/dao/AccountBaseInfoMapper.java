package stark.stellasearch.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import stark.stellasearch.domain.AccountBaseInfo;

import java.util.List;

@Mapper
public interface AccountBaseInfoMapper
{
    AccountBaseInfo getAccountByUsername(String username);
    int updatePasswordByUsername(@Param("username") String username, @Param("encryptedPassword") String encryptedPassword);
    List<AccountBaseInfo> getByUsernamePhoneNumberEmail(@Param("username") String username, @Param("phoneNumber") String phoneNumber, @Param("phoneNumberPrefix") String phoneNumberPrefix, @Param("email") String email);
    int insert(AccountBaseInfo accountBaseInfo);
    Long getUserIdByUsername(String username);
}