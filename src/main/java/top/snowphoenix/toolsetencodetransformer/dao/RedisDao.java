package top.snowphoenix.toolsetencodetransformer.dao;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisDao {
    public RedisDao(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    private final StringRedisTemplate redisTemplate;

    public StringRedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    @Transactional
    public void setList(String key, Collection<String> list, long timeout, TimeUnit timeUnit) {
        redisTemplate.delete(key);
        redisTemplate.opsForList().rightPushAll(key, list);
        redisTemplate.expire(key, timeout, timeUnit);
    }

    public List<String> getList(String key) {
        return redisTemplate.opsForList().range(key, 0, -1);
    }

    @Transactional
    public void setHash(String key, Map<String, String> map, long timeout, TimeUnit timeUnit) {
        redisTemplate.delete(key);
        getRedisTemplate().opsForHash().putAll(key, map);
        redisTemplate.expire(key, timeout, timeUnit);
    }

    public <T> void setHashValue(String key, String hashKey, T value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }

    public String getHashValue(String key, String hashKey) {
        return redisTemplate.<String, String>opsForHash().get(key, hashKey);
    }

    public Map<String, String> getHash(String key) {
        return redisTemplate.<String, String>opsForHash().entries(key);
    }

    @Transactional
    public <T> void setValue(String key, T value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value.toString());
        redisTemplate.expire(key, timeout, timeUnit);
    }

    public <T> void setValue(String key, T value) {
        redisTemplate.opsForValue().set(key, value.toString());
    }

    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 将某个key的超时时间设置为指定时间，返回所指定的key是否存在。
     * 在开启事务时调用它，将永远返回{@code false}，但该结果并不表示目标的状态。
     *
     * @param key 需要操作的key
     * @param timeout 超时时间
     * @param timeUnit 超时时间单位
     * @return 目标是否存在并未超时。若在事务中，则返回{@code false}。
     */
    public boolean setExpire(String key, long timeout, TimeUnit timeUnit) {
        Boolean res = redisTemplate.expire(key, timeout, timeUnit);
        if (res != null) {
            return res;
        }
        return false;
    }
}
