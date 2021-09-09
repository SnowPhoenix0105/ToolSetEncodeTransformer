package top.snowphoenix.toolsetencodetransformer.model;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public enum Encoding {
    UNKNOWN(null),
    UTF_8(StandardCharsets.UTF_8),
    GBK(Charset.forName("GBK"))
    ;

    Encoding(Charset charset) {
        this.charset = charset;
    }

    private final Charset charset;

    public Charset getCharset() {
        return charset;
    }
}
