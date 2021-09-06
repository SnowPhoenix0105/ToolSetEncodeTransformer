package top.snowphoenix.toolsetencodetransformer.dao;

import lombok.var;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Component
public class RedisDao {
    private StringRedisTemplate redisTemplate;

    @Transactional
    public void setList(String key, Iterable<String> list, long timeout, TimeUnit timeUnit) {
        redisTemplate.delete(key);
        var listOps = redisTemplate.opsForList();
        for (var str : list) {
            listOps.rightPush(key, str);
        }
        redisTemplate.expire(key, timeout, timeUnit);
    }
}
