package top.snowphoenix.toolsetencodetransformer.service;

import lombok.var;
import org.springframework.stereotype.Service;
import top.snowphoenix.toolsetencodetransformer.exception.TimeoutException;
import top.snowphoenix.toolsetencodetransformer.manager.CacheManager;
import top.snowphoenix.toolsetencodetransformer.manager.FilePathManager;
import top.snowphoenix.toolsetencodetransformer.model.Encoding;
import top.snowphoenix.toolsetencodetransformer.utils.charset.CharSet;
import top.snowphoenix.toolsetencodetransformer.utils.charset.CharSetUtil;
import top.snowphoenix.toolsetencodetransformer.utils.charset.CharSetWorker;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

@Service
public class EncodingService {
    public EncodingService(CharSetUtil charSetUtil, FilePathManager filePath, CacheManager cacheManager) {
        this.charSetUtil = charSetUtil;
        this.filePath = filePath;
        this.cacheManager = cacheManager;
    }

    private final CharSetUtil charSetUtil;
    private final FilePathManager filePath;
    private final CacheManager cacheManager;

    public Map<Integer, Encoding> judgeAndTransform(
            int uid,
            Set<CharSet> selectedCharSets,
            List<Integer> selectedFids,
            Encoding targetEncoding) throws IOException, TimeoutException {
        if (!cacheManager.refreshExpire(uid)) {
            throw new TimeoutException();
        }
        cacheManager.setTargetEncoding(uid, targetEncoding);
        cacheManager.setCharSets(uid, selectedCharSets);

        Path targetDir = filePath.transformedFileDir(uid);
        filePath.ensureAndClearDir(targetDir);

        var workers = charSetUtil.getWorkers(selectedCharSets);

        HashMap<Integer, Encoding> ret = new HashMap<>();
        for (int fid : selectedFids) {
            Encoding encoding = judgeAndTransform(uid, fid, workers, targetEncoding);
            ret.put(fid, encoding);
        }

        cacheManager.setFileEncodings(uid, ret);
        return ret;
    }

    public void modifyAndTransform(int uid, int fid, Encoding newEncoding)
            throws TimeoutException, IOException {
        if (!cacheManager.refreshExpire(uid)) {
            throw new TimeoutException();
        }
        Encoding oldEncoding = cacheManager.getFileEncoding(uid, fid);
        if (oldEncoding == newEncoding) {
            return;
        }
        cacheManager.setFileEncoding(uid, fid, newEncoding);

        Path src = filePath.originFile(uid, fid);
        List<String> lines = Files.readAllLines(src, newEncoding.getCharset());

        Path dst = filePath.transformedFile(uid, fid);
        Encoding targetEncoding = cacheManager.getTargetEncoding(uid);
        Files.write(dst, lines, targetEncoding.getCharset());
    }

    private Encoding judgeAndTransform(
            int uid,
            int fid,
            List<CharSetWorker> workers,
            Encoding targetEncoding) throws IOException {
        Path srcPath = filePath.originFile(uid, fid);
        byte[] bytes = Files.readAllBytes(srcPath);

        double maxCorrectRate = 0;
        CharBuffer maxCorrectCharBuffer = null;
        Encoding maxCorrectEncoding = Encoding.UNKNOWN;
        for (Encoding encoding : Encoding.values()) {
            if (encoding == Encoding.UNKNOWN) {
                continue;
            }
            CharBuffer charBuffer = encoding.getCharset().decode(ByteBuffer.wrap(bytes));
            int successCount = 0;
            char[] chars = charBuffer.array();
            int limit = charBuffer.limit();
            for (int i = 0; i < limit; i ++) {
                char c = chars[i];
                for (var worker : workers) {
                    if (worker.contains(c)) {
                        successCount++;
                        break;
                    }
                }
            }
            if (successCount == limit) {
                maxCorrectRate = 1;
                maxCorrectCharBuffer = charBuffer;
                maxCorrectEncoding = encoding;
                break;
            }
            double correctRate = successCount / (double) limit;
            if (correctRate >= maxCorrectRate) {
                maxCorrectRate = correctRate;
                maxCorrectCharBuffer = charBuffer;
                maxCorrectEncoding = encoding;
            }
        }

        Path dstPath = filePath.transformedFile(uid, fid);

        if (maxCorrectCharBuffer != null) {
            ByteBuffer bb = targetEncoding.getCharset().encode(maxCorrectCharBuffer);
            byte[] exactBytes = new byte[bb.limit()];
            bb.get(exactBytes);
            Files.write(dstPath,
                    exactBytes,
                    StandardOpenOption.CREATE);
        }

        return maxCorrectEncoding;
    }
}
