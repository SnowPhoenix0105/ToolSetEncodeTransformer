package top.snowphoenix.toolsetencodetransformer.utils.charset;

import lombok.var;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/***
 * 通过枚举字符的方法创建的字符集工作类
 */
class ArrayCharSetWorker implements CharSetWorker {
    /***
     * 将目标文件中的所有字符作为字符集的内容
     *
     * @param path 文件路径
     * @return 文件中字符构成的字符集工作类
     * @throws IOException 读取文件时发生了io错误
     */
    public static ArrayCharSetWorker fromFile(String path) throws IOException {
        var lines = Files.readAllLines(Paths.get(path));
        String all;
        if (lines.size() == 1) {
            all = lines.get(0);
        }
        else {
            var sb = new StringBuilder();
            for (var line : lines) {
                sb.append(line);
            }
            all = sb.toString();
        }
        var set = new HashSet<Character>(all.length());
        for (char c : all.toCharArray()) {
            set.add(c);
        }
        return new ArrayCharSetWorker(set, all);
    }

    private ArrayCharSetWorker(Set<Character> set, String all) {
        this.set = set;
        this.all = all;
    }

    public ArrayCharSetWorker(Collection<Character> set) {
        this.set = new HashSet<>(set.size());
        var sb = new StringBuilder();
        for (char c : set) {
            this.set.add(c);
            sb.append(c);
        }
        all = sb.toString();
    }

    public ArrayCharSetWorker(char...set) {
        this.set = new HashSet<>(set.length);
        var sb = new StringBuilder();
        for (char c : set) {
            this.set.add(c);
            sb.append(c);
        }
        all = sb.toString();
    }

    private final Set<Character> set;
    private final String all;

    @Override
    public boolean contains(char c) {
        return set.contains(c);
    }

    @Override
    public void printAll(StringBuilder stringBuilder) {
        stringBuilder.append(all);
    }
}
