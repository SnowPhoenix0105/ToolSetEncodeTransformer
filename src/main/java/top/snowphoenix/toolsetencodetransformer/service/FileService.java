package top.snowphoenix.toolsetencodetransformer.service;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.snowphoenix.toolsetencodetransformer.config.FileConfig;
import top.snowphoenix.toolsetencodetransformer.model.FileInfo;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class FileService {
    // TODO Constructor

    private StringRedisTemplate redisTemplate;
    private FileConfig fileConfig;

    private ArrayList<FileInfo> saveFiles(Path workDirPath, MultipartFile[] files) throws IOException {
        Files.deleteIfExists(workDirPath);
        Files.createDirectories(workDirPath);

        ArrayList<FileInfo> fileInfos = new ArrayList<>();
        try {
            for (int i = 0; i < files.length; i++) {
                var file = files[i];
                String name = file.getOriginalFilename();
                if (name == null) {
                    name = file.getName();
                }
                Path target = workDirPath.resolve(String.valueOf(i));
                Files.copy(file.getInputStream(), target);
                fileInfos.add(FileInfo.builder()
                        .fid(i)
                        .name(name)
                        .build());
            }
        }
        catch (Exception e) {
            log.warn("save(int, MultipartFile[]) wrong with file operations", e);
            Files.deleteIfExists(workDirPath);
            throw e;
        }
        return fileInfos;
    }

    public ArrayList<FileInfo> save(int uid, MultipartFile[] files) throws IOException {
        Path workDirPath = Paths.get(fileConfig.getWorkDir(), String.valueOf(uid));

        var fileInfos = saveFiles(workDirPath, files);

        redisTemplate.multi();
        try {
            String key = "files:" + uid;
            redisTemplate.delete(key);
            var listOps = redisTemplate.opsForList();
            for (var fileInfo : fileInfos) {
                listOps.rightPush(key, fileInfo.getName());
            }
            redisTemplate.expire(key, 30, TimeUnit.MINUTES);
        }
        catch (Exception e) {
            log.warn("save(int, MultipartFile[]) wrong with redis operations", e);
            redisTemplate.discard();
            Files.deleteIfExists(workDirPath);
            throw e;
        }
        finally {
            redisTemplate.exec();
        }
        return fileInfos;
    }
}
