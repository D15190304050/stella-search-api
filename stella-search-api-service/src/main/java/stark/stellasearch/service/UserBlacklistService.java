package stark.stellasearch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stark.stellasearch.dao.UserBlacklistMapper;

@Slf4j
@Service
public class UserBlacklistService
{
    @Autowired
    private UserBlacklistMapper userBlacklistMapper;

}
