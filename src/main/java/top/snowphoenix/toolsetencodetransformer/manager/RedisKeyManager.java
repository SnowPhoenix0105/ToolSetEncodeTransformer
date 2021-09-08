package top.snowphoenix.toolsetencodetransformer.manager;

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
}
