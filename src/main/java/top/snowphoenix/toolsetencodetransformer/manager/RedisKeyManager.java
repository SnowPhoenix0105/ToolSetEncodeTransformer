package top.snowphoenix.toolsetencodetransformer.manager;

import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class RedisKeyManager {
    public String fileList(int uid) {
        return "files:" + uid;
    }

    public String fileSelectHash(int uid) {
        return "selected:" + uid;
    }

    public String targetEncoding(int uid) {
        return "target" + uid;
    }

    public String charsetList(int uid) {
        return "charset:" + uid;
    }

    public Iterable<String> allKeysForUser(int uid) {
        return Arrays.asList(
                fileList(uid),
                fileSelectHash(uid),
                targetEncoding(uid),
                charsetList(uid)
        );
    }
}
