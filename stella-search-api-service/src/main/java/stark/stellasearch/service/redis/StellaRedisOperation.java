package stark.stellasearch.service.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import stark.dataworks.basic.data.redis.RedisQuickOperation;
import stark.stellasearch.service.JwtService;
import stark.stellasearch.service.dto.User;

import java.util.concurrent.TimeUnit;

@Component
public class StellaRedisOperation
{
    @Autowired
    private RedisQuickOperation redisQuickOperation;

    @Autowired
    private RedisKeyManager redisKeyManager;

    public void cacheUser(User user)
    {
        long userId = user.getId();
        String userIdKey = redisKeyManager.getUserIdKey(userId);
        redisQuickOperation.set(userIdKey, user, JwtService.TOKEN_EXPIRATION_IN_DAYS, TimeUnit.DAYS);
    }
}
