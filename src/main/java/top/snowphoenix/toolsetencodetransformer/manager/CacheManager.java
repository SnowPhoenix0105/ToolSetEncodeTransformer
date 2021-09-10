package top.snowphoenix.toolsetencodetransformer.manager;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import top.snowphoenix.toolsetencodetransformer.config.RedisConfig;
import top.snowphoenix.toolsetencodetransformer.dao.RedisDao;
import top.snowphoenix.toolsetencodetransformer.model.Encoding;
import top.snowphoenix.toolsetencodetransformer.model.FileInfo;
import top.snowphoenix.toolsetencodetransformer.utils.charset.CharSet;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CacheManager {
    public CacheManager(RedisDao redisDao, RedisKeyManager redisKey, RedisConfig redisConfig) {
        this.redisDao = redisDao;
        this.redisKey = redisKey;
        this.redisConfig = redisConfig;
    }

    private final RedisDao redisDao;
    private final RedisKeyManager redisKey;
    private final RedisConfig redisConfig;

    public void setFileInfos(int uid, List<FileInfo> fileInfos) {
        String key = redisKey.fileList(uid);
        redisDao.setList(
                key,
                fileInfos.stream()
                        .map(FileInfo::getName)
                        .collect(Collectors.toList()),
                redisConfig.getTimeoutMinute(),
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

    public void setTargetEncoding(int uid, Encoding encoding) {
        redisDao.setValue(
                redisKey.targetEncoding(uid),
                encoding.ordinal(),
                redisConfig.getTimeoutMinute(),
                TimeUnit.MINUTES);
    }

    public Encoding getTargetEncoding(int uid) {
        String res = redisDao.getValue(redisKey.targetEncoding(uid));
        return Encoding.values()[Integer.parseInt(res)];
    }

    public void setFileEncoding(int uid, int fid, Encoding encoding) {
        redisDao.setHashValue(
                redisKey.fileSelectHash(uid),
                String.valueOf(fid),
                String.valueOf(encoding.ordinal()));
    }

    public Encoding getFileEncoding(int uid, int fid) {
        String res = redisDao.getHashValue(redisKey.fileSelectHash(uid), String.valueOf(fid));
        return Encoding.values()[Integer.parseInt(res)];
    }

    public void setCharSets(int uid, Collection<CharSet> charSets) {
        redisDao.setList(
                redisKey.charsetList(uid),
                charSets.stream()
                        .map(cs -> String.valueOf(cs.getCid()))
                        .collect(Collectors.toList()),
                redisConfig.getTimeoutMinute(),
                TimeUnit.MINUTES);
    }

    public Set<CharSet> getCharSets(int uid) {
        return redisDao.getList(redisKey.charsetList(uid))
                .stream()
                .map(s -> CharSet.ofCid(Integer.parseInt(s)))
                .collect(Collectors.toSet());
    }

    /***
     * 保存用户选择的文件和其编码信息
     *
     * @param uid 用户ID
     * @param fidEncodingMap 文件ID及其对应编码
     */
    public void setFileEncodings(int uid, Map<Integer, Encoding> fidEncodingMap) {
        redisDao.setHash(
                redisKey.fileSelectHash(uid),
                fidEncodingMap
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                e -> e.getKey().toString(),
                                e-> String.valueOf(e.getValue().ordinal()))),
                redisConfig.getTimeoutMinute(),
                TimeUnit.MINUTES);
    }

    public Map<Integer, Encoding> getFileEncodings(int uid) {
        return redisDao
                .getHash(redisKey.fileSelectHash(uid))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        e -> Integer.parseInt(e.getKey()),
                        e -> Encoding.values()[Integer.parseInt(e.getValue())]))
                ;
    }

    /***
     * 将用户的全部信息的超时时间进行刷新
     *
     * @param uid 用户ID
     * @return 若用户的全部信息都失效，则返回false
     */
    public boolean refreshExpire(int uid) {
        boolean ret = false;
        for (String k : redisKey.allKeysForUser(uid)) {
            ret = (ret || redisDao.setExpire(k, redisConfig.getTimeoutMinute(), TimeUnit.MINUTES));
        }
        return ret;
    }
}
