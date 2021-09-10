package top.snowphoenix.toolsetencodetransformer.model;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public enum Encoding {
    UNKNOWN(null, "unknow"),
    UTF_8(StandardCharsets.UTF_8, "utf8"),
    GBK(Charset.forName("GBK"), "gbk")
    ;

    Encoding(Charset charset, String name) {
        this.charset = charset;
        this.name = name;
    }

    private static final Map<String, Encoding> stringMap = new HashMap<String, Encoding>(){{
        put("gbk", Encoding.GBK);
        put("utf8", Encoding.UTF_8);
        put("utf-8", Encoding.UTF_8);
        put("utf_8", Encoding.UTF_8);
    }};

    public static Encoding parse(String str) {
        return stringMap.get(str.toLowerCase());
    }

    private final String name;
    private final Charset charset;

    public String getName() {
        return name;
    }

    public Charset getCharset() {
        return charset;
    }
}
