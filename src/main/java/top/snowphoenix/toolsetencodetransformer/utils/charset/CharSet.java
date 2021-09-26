package top.snowphoenix.toolsetencodetransformer.utils.charset;

import lombok.var;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public enum CharSet {
    LOWER,
    UPPER,
    NUMBER,
    SYMBOL,
    COMMON_LATIN,
    BASIC_CHINESE,
    COMMON_CHINESE_3500,
    COMMON_CHINESE_7000,
    CHINESE_SYMBOL
    ;

    private static final Map<CharSet, String> desc = new HashMap<CharSet, String>(){{
        put(LOWER, "小写英文字母");
        put(UPPER, "大写英文字母");
        put(NUMBER, "数字");
        put(SYMBOL, "常见符号");
        put(COMMON_LATIN, "包括大小写英文字母、数字、常见符号");
        put(BASIC_CHINESE, "汉字基础集合，Unicode从4E00至9FA5");
        put(COMMON_CHINESE_3500, "汉字通用集合，3500字");
        put(COMMON_CHINESE_7000, "汉字通用集合，7000字");
        put(CHINESE_SYMBOL, "中文符号");
    }};

    private static final Map<CharSet, String> name = new HashMap<CharSet, String>(){{
        put(LOWER, "小写英文字母");
        put(UPPER, "大写英文字母");
        put(NUMBER, "数字");
        put(SYMBOL, "常见符号");
        put(COMMON_LATIN, "常用LATIN");
        put(BASIC_CHINESE, "基本汉字");
        put(COMMON_CHINESE_3500, "通用汉字3500");
        put(COMMON_CHINESE_7000, "通用汉字7000");
        put(CHINESE_SYMBOL, "中文符号，全角符号");
    }};

    public static void checkSetContainsAll(String name, Iterable<CharSet> iterable) {
        var set = new HashSet<>(Arrays.asList(CharSet.values()));
        for (CharSet cs : iterable) {
            set.remove(cs);
        }
        if (!set.isEmpty()) {
            throw new RuntimeException(name + " doesn't contain all elements in enum CharSet");
        }
    }

    static {
        checkSetContainsAll("desc", desc.keySet());
        checkSetContainsAll("name", name.keySet());
    }

    public int getCid() {
        return this.ordinal();
    }

    public static CharSet ofCid(int cid) {
        var values = CharSet.values();
        if (cid < values.length) {
            return values[cid];
        }
        return null;
    }

    public String getName() {
        return name.get(this);
    }

    public String getDesc() {
        return desc.get(this);
    }
}
