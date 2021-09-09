package top.snowphoenix.toolsetencodetransformer.service;

import lombok.var;
import org.springframework.stereotype.Service;
import top.snowphoenix.toolsetencodetransformer.config.FileConfig;
import top.snowphoenix.toolsetencodetransformer.manager.CacheManager;
import top.snowphoenix.toolsetencodetransformer.model.Encoding;
import top.snowphoenix.toolsetencodetransformer.utils.charset.CharSet;
import top.snowphoenix.toolsetencodetransformer.utils.charset.CharSetUtil;
import top.snowphoenix.toolsetencodetransformer.utils.charset.CharSetWorker;

import java.io.BufferedReader;
import java.io.CharArrayReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class EncodingJudgementService {
    // TODO Constructor

    private CharSetUtil charSetUtil;
    private FileConfig fileConfig;
    private CacheManager cacheManager;

    public Map<Integer, Map<Encoding, Double>> judge(int uid, Set<CharSet> selectedCharSets, List<Integer> selectedFids) throws IOException {
        HashMap<Integer, Map<Encoding, Double>> ret = new HashMap<>();

        var workers = charSetUtil.getWorkers(selectedCharSets);

        for (int fid : selectedFids) {
            var map = judgeFile(uid, fid, workers);
            ret.put(fid, map);
        }

        return ret;
    }

    private Map<Encoding, Double> judgeFile(int uid, int fid, List<CharSetWorker> workers) throws IOException {
        HashMap<Encoding, Double> map = new HashMap<>();

        Path filePath = Paths.get(fileConfig.getWorkDir(), String.valueOf(uid), String.valueOf(fid));
        byte[] bytes = Files.readAllBytes(filePath);

        for (Encoding encoding : Encoding.values()) {
            if (encoding == Encoding.UNKNOWN) {
                continue;
            }
            CharBuffer charBuffer = encoding.getCharset().decode(ByteBuffer.wrap(bytes));
            int successCount = 0;
            char[] chars = charBuffer.array();
            for (char c : chars) {
                for (var worker : workers) {
                    if (worker.contains(c)) {
                        successCount++;
                        break;
                    }
                }
            }
            map.put(encoding, successCount / (double) chars.length);
        }

        return map;
    }
}
