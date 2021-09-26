package top.snowphoenix.toolsetencodetransformer.utils.charset;

import org.springframework.stereotype.Component;
import top.snowphoenix.toolsetencodetransformer.config.CharSetConfig;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class CharSetUtil {
    public CharSetUtil(CharSetConfig config) throws IOException {
        workers = new EnumMap<>(CharSet.class);

        workers.put(CharSet.LOWER, new RangeCharSetWorker('a', 'z'));
        workers.put(CharSet.UPPER, new RangeCharSetWorker('A', 'Z'));
        workers.put(CharSet.NUMBER, new RangeCharSetWorker('0', '9'));
        workers.put(CharSet.SYMBOL, new ArrayCharSetWorker(new ArrayList<Character>() {{
            addAll(IntStream.range(' ', '/' + 1).mapToObj(i -> (char) i).collect(Collectors.toList()));
            addAll(IntStream.range(':', '@' + 1).mapToObj(i -> (char) i).collect(Collectors.toList()));
            addAll(IntStream.range('[', '`' + 1).mapToObj(i -> (char) i).collect(Collectors.toList()));
            addAll(IntStream.range('{', '~' + 1).mapToObj(i -> (char) i).collect(Collectors.toList()));
            addAll(Arrays.asList('\t', '\n', '\r'));
        }}));
        workers.put(CharSet.COMMON_LATIN, new ArrayCharSetWorker(new ArrayList<Character>() {{
            addAll(IntStream.range(' ', '~' + 1).mapToObj(i -> (char) i).collect(Collectors.toList()));
            addAll(Arrays.asList('\t', '\n', '\r'));
        }}));
        workers.put(CharSet.BASIC_CHINESE, new RangeCharSetWorker('一', '龥'));
        workers.put(CharSet.COMMON_CHINESE_3500, ArrayCharSetWorker.fromFile(config.getCommon3500()));
        workers.put(CharSet.COMMON_CHINESE_7000, ArrayCharSetWorker.fromFile(config.getCommon7000()));
        workers.put(CharSet.CHINESE_SYMBOL, new ArrayCharSetWorker(
                '–', '—', '‘', '’', '“', '”',
                '…', '、', '。', '〈', '〉', '《',
                '》', '「', '」', '『', '』', '【',
                '】', '〔', '〕', '！', '（', '）',
                '，', '．', '：', '；', '？'));

        CharSet.checkSetContainsAll("CharSetUtil.workers", workers.keySet());
    }

    private final Map<CharSet, CharSetWorker> workers;
    public final static List<CharSet> COMMON_LATIN_CONTAINS = Arrays.asList(
            CharSet.UPPER,
            CharSet.LOWER,
            CharSet.NUMBER,
            CharSet.SYMBOL
    );

    public ArrayList<CharSetWorker> getWorkers(Set<CharSet> charSets) {
        ArrayList<CharSetWorker> ret = new ArrayList<>();
        if (charSets.remove(CharSet.COMMON_LATIN) || charSets.containsAll(COMMON_LATIN_CONTAINS)) {
            COMMON_LATIN_CONTAINS.forEach(charSets::remove);
            ret.add(workers.get(CharSet.COMMON_LATIN));
        }
        for (CharSet charSet : charSets) {
            ret.add(workers.get(charSet));
        }
        return ret;
    }

    public CharSetWorker getWorker(CharSet charSet) {
        return workers.get(charSet);
    }

    public boolean contains(CharSet charSet, char c) {
        return workers.get(charSet).contains(c);
    }

    public void printAll(CharSet charSet, StringBuilder stringBuilder) {
        workers.get(charSet).printAll(stringBuilder);
    }
}
