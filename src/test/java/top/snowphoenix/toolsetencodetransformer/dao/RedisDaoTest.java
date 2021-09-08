package top.snowphoenix.toolsetencodetransformer.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisDaoTest {
    @Autowired
    ApplicationContext applicationContext;

    @Test
    void getAndSetStringHash() {
        RedisDao dao = applicationContext.getBean(RedisDao.class);
        Map<String, String> map = IntStream
                .range(10, 30)
                .boxed()
                .collect(Collectors.toMap(
                        String::valueOf,
                        String::valueOf))
                ;
        String key = "getAndSetStringHash";
        dao.setHash(key, map, 30, TimeUnit.SECONDS);
        Map<String, String> res = dao.getHash(key);
        assertEquals(map, res);
    }
}