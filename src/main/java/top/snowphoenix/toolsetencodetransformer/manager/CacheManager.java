package top.snowphoenix.toolsetencodetransformer.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.snowphoenix.toolsetencodetransformer.config.FileConfig;
import top.snowphoenix.toolsetencodetransformer.dao.RedisDao;
import top.snowphoenix.toolsetencodetransformer.model.Encoding;
import top.snowphoenix.toolsetencodetransformer.model.FileInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CacheManager {
    // TODO Constructor

    private RedisDao redisDao;
    private RedisKeyManager redisKey;
    private FileConfig fileConfig;

    public void setFileInfos(int uid, List<FileInfo> fileInfos) {
        String key = redisKey.fileList(uid);
        redisDao.setList(
                key,
                fileInfos.stream().map(FileInfo::getName).collect(Collectors.toList()),
                fileConfig.getTimeoutMinute(),
                TimeUnit.MINUTES);
    }

    public List<FileInfo> getFileInfos(int uid) {
        String key = redisKey.fileList(uid);
        AtomicInteger fid = new AtomicInteger();
        return redisDao.getList(key)
                .stream()
                .map(n -> FileInfo.builder()
                        .name(n)
                        .fid(fid.getAndIncrement())
                        .build())
                .collect(Collectors.toList());
    }

    public void setSelectFiles(int uid, Map<Integer, Encoding> fidEncodingMap) {
        // TODO
    }

    public Map<Integer, Encoding> getSelectedFiles(int uid) {
        // TODO
        return null;
    }


}
